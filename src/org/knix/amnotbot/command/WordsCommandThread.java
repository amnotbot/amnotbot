package org.knix.amnotbot.command;

import org.knix.amnotbot.command.utils.CmdCommaSeparatedOption;
import org.knix.amnotbot.command.utils.CommandOptions;
import org.knix.amnotbot.command.utils.CmdStringOption;
import org.knix.amnotbot.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;

import javax.naming.directory.InvalidAttributeValueException;
import org.knix.amnotbot.command.utils.CmdOption;
import org.knix.amnotbot.config.BotConfiguration;

public class WordsCommandThread extends Thread
{
    
    BotMessage msg;
    CommandOptions opts;    

    public enum countOperation {
        WORDS, LINES
    }
    countOperation countOp;

    public WordsCommandThread(BotMessage msg, countOperation op)
    {
        this.msg = msg;
        this.countOp = op;

        opts = new CommandOptions(msg.getText());

        opts.addOption(new CmdCommaSeparatedOption("nick"));
        opts.addOption(new CmdCommaSeparatedOption("word"));
        opts.addOption(new CmdStringOption("number"));
        opts.addOption(new CmdStringOption("date"));
        opts.addOption(new CmdStringOption("op"));
        opts.addOption(new CmdStringOption("channel"));

        start();
    }

    public void run()
    {
        this.init();

        String db_file = null;
        try {
            db_file = this.selectDBFile();
        } catch (Exception e) {
            BotLogger.getDebugLogger().debug(e.getMessage());
            e.printStackTrace();
            return;
        }
        
        this.processRequest( this.selectBackend(db_file) );
    }

    private void init()
    {
        this.opts.buildArgs();
    }

    private String selectDBFile()
            throws FileNotFoundException, InvalidAttributeValueException
    {
        String target = this.msg.getTarget();
        if (this.opts.getOption("channel").hasValue()) {
            target = this.opts.getOption("channel").stringValue();
        }

        if (target.charAt(0) != '#') {
            throw new InvalidAttributeValueException("Not a valid channel: " +
                    target + "). Use the 'channel:' option.");
        }

        String db_file = this.msg.getConn().getBotLogger().getLoggingPath() +
                "/" + target;
        if (!this.dbExists(db_file)) {
            throw new FileNotFoundException("Statistics not available for: " +
                    target);
        }
        return db_file;
    }

    boolean dbExists(String path)
    {
        File db_file = new File(path);

        if (!db_file.exists()) return false;

        return true;
    }

    private WordCounter selectBackend(String db_file)
    {
        String wCounter;
        wCounter = BotConfiguration.getConfig().getString("word_counter_imp");
        if (wCounter.compareTo("sqlite") == 0) {
            return ( new WordCounterSqlite(db_file) );
        }

        String i_file;
        i_file = BotConfiguration.getConfig().getString("ignored_words_file");
        return (new WordCounterTextFile(i_file, db_file) );
    }

    private void processRequest(WordCounter wordCounter)
    {
        String num = this.opts.getOption("number").stringValue();
        int n = num == null ? 5 : Integer.parseInt(num);

        String nickList = null;
        if (opts.getOption("nick").hasValue()) {
            CmdOption nickOpt = this.opts.getOption("nick");
            nickList = nickOpt.stringValue(" ").toLowerCase();
        }

        WordResults results = new WordResults();
        switch (this.countOp) {
            case WORDS:
                results = this.countWords(wordCounter, n, nickList);
                break;
            case LINES:
                results = this.countLines(wordCounter, n, nickList);
                break;
        }
        this.showResults(results);
    }

    private WordResults countWords(WordCounter wordCounter,
            int n,
            String nickList)
    {
        String words;
        WordResults results = new WordResults();
        if (this.opts.getOption("word").hasValue()) {
            words = wordCounter.mostUsedWordsBy(n,
                    this.opts.getOption("word").stringValue(" ").trim(),
                    this.opts.getOption("date").stringValue());
        } else {
            words = wordCounter.mostUsedWords(n, nickList,
                    this.opts.getOption("date").stringValue());
        }
        results.setOutputMessage("Most used words for ");
        results.setWords(words);

        return results;
    }

    private WordResults countLines(WordCounter wordCounter,
            int n,
            String nickList)
    {
        String op = "";
        if (this.opts.getOption("op").hasValue()) {
            op = this.opts.getOption("op").stringValue();
        }

        String words;
        WordResults results = new WordResults();
        if (op.compareTo("avg") == 0) {
            words = wordCounter.avgWordsLine(n, nickList,
                    this.opts.getOption("date").stringValue());
            results.setOutputMessage("Avg. words per line per user for ");
        } else {
            words = wordCounter.topLines(n,
                    this.opts.getOption("date").stringValue());
            results.setOutputMessage("Lines per user for ");
        }
        results.setWords(words);
        
        return results;
    }

    private void showResults(WordResults results)
    {
        String target;
        BotConnection conn;

        conn = this.msg.getConn();
        target = this.msg.getTarget();
        if (!results.hasResults()) {
            conn.doPrivmsg(target, "Could not find any match!");
            return;
        }

        LinkedList<String> output = this.truncateOutput(results);
        if (opts.getOption("nick").hasValue()) {
            conn.doPrivmsg(target, results.getOutputMessage() + "'" +
                    opts.getOption("nick").stringValue().trim() +
                    "': " + output.getFirst());
        } else {
            conn.doPrivmsg(target, results.getOutputMessage() + "'" +
                    target + "': " + output.getFirst());
        }

        for (int j = 1; j < output.size(); ++j) {
            conn.doPrivmsg(target, output.get(j));
            try {
                // avoid being disconnected by flooding
                Thread.sleep(300 * j);
            } catch (InterruptedException e) {
                BotLogger.getDebugLogger().debug(e.getMessage());
                break;
            }
        }
    }

    private LinkedList<String> truncateOutput(WordResults results)
    {
        LinkedList<String> wList = new LinkedList<String>();
        // irc client truncates everything over 440 chars
        int position = 0, maxChars = 430;
        String words = results.getWords();
        int wordsLength = results.getWords().length();
        int msgLength = results.getOutputMessage().length();
        while ((wordsLength + msgLength) - position > maxChars) {
            int truncPosition;
            truncPosition = words.indexOf(' ', (maxChars / 2) + position);
            wList.add(words.substring(position, truncPosition));
            position = truncPosition;
        }
        wList.add(words.substring(position, wordsLength));        
        return wList;
    }

    private class WordResults
    {
        private String words;
        private String outputMessage;

        WordResults() 
        {
            this.words = null;
            this.outputMessage = null;
        }
                
        WordResults(String words, String outputMessage)
        {
            this.words = words;
            this.outputMessage = outputMessage;
        }

        public boolean hasResults()
        {
            return (this.words != null);
        }

        public String getWords()
        {
            return this.words;
        }

        public String getOutputMessage()
        {
            return this.outputMessage;
        }

        public void setWords(String words)
        {
            this.words = words;
        }

        public void setOutputMessage(String msg)
        {
            this.outputMessage = msg;
        }
    }
}
