package com.github.amnotbot.cmd;

import java.io.FileNotFoundException;
import java.util.LinkedList;

import javax.naming.directory.InvalidAttributeValueException;
import org.apache.commons.lang.StringUtils;

import com.github.amnotbot.BotConnection;
import com.github.amnotbot.BotLogger;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.cmd.db.BotDBFactory;
import com.github.amnotbot.cmd.db.WordCounterDAO;
import com.github.amnotbot.cmd.utils.CmdOptionImp;
import com.github.amnotbot.cmd.utils.CommandOptions;

public class WordsCommandImp
{
    
    BotMessage msg;
    CommandOptions opts;

    public enum countOperation {
        WORDS, LINES, UNIQUEWORDS
    }
    countOperation countOp;

    public WordsCommandImp(BotMessage msg, countOperation op)
    {
        this.msg = msg;
        this.countOp = op;

        opts = new CommandOptions(msg.getText());

        opts.addOption(new CmdOptionImp("nick", ","));
        opts.addOption(new CmdOptionImp("word", ","));
        opts.addOption(new CmdOptionImp("number"));
        opts.addOption(new CmdOptionImp("date"));
        opts.addOption(new CmdOptionImp("op"));
        opts.addOption(new CmdOptionImp("channel"));
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

        WordCounterDAO wCounter = null;
        wCounter = BotDBFactory.instance().createWordCounterDAO(db_file);

        this.processRequest( wCounter );
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
            target = this.opts.getOption("channel").tokens()[0];
        }

        if (target.charAt(0) != '#') {
            throw new InvalidAttributeValueException("Not a valid channel: " +
                    target + "). Use the 'channel:' option.");
        }

        String db_file = this.msg.getConn().getBotLogger().getLoggingPath() +
                "/" + target + ".db";

        return db_file;
    }

    private void processRequest(WordCounterDAO wordCounter)
    {
        String num;
        String [] nicks;

        num = this.opts.getOption("number").tokens()[0];
        int n = StringUtils.isBlank(num) ? 5 : Integer.parseInt(num);
        nicks = this.opts.getOption("nick").tokens();

        WordResults results = new WordResults();
        switch (this.countOp) {
            case WORDS:
                results = this.countWords(wordCounter, n, nicks);
                break;
            case LINES:
                results = this.countLines(wordCounter, n, nicks);
                break;
            case UNIQUEWORDS:
                results = this.countUniqueWords(wordCounter, n, nicks);
                break;
        }
        this.showResults(results);
    }

    private WordResults countWords(WordCounterDAO wordCounter,
            int n,
            String [] nicks)
    {
        String words;
        WordResults results = new WordResults();
        if (this.opts.getOption("word").hasValue()) {
            words = wordCounter.mostUsedWordsBy(n,
                    this.opts.getOption("word").tokens(),
                    this.opts.getOption("date").tokens()[0]);
        } else {
            words = wordCounter.mostUsedWords(n, nicks,
                    this.opts.getOption("date").tokens()[0]);
        }
        results.setOutputMessage("Most used words for ");
        results.setWords(words);
        return results;
    }

    private WordResults countLines(WordCounterDAO wordCounter,
            int n,
            String [] nicks)
    {
        String op = "";
        if (this.opts.getOption("op").hasValue()) {
            op = this.opts.getOption("op").tokens()[0];
        }

        String words;
        WordResults results = new WordResults();
        if (op.compareTo("avg") == 0) {
            words = wordCounter.avgWordsLine(n, nicks,
                    this.opts.getOption("date").tokens()[0]);
            results.setOutputMessage("Avg. words per line per user for ");
        } else {
            words = wordCounter.topLines(n,
                    this.opts.getOption("date").tokens()[0]);
            results.setOutputMessage("Lines per user for ");
        }
        results.setWords(words);
        
        return results;
    }
    
    private WordResults countUniqueWords(WordCounterDAO wordCounter, 
            int n, 
            String[] nicks) 
    {
        String words;
        WordResults results = new WordResults();
        
        words = wordCounter.countUniqueWords(n, nicks, this.opts.getOption("date").tokens()[0]);
        
        results.setOutputMessage("Unique words for ");
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
                    opts.getOption("nick").tokens()[0] +
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
