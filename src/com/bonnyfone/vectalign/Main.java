package com.bonnyfone.vectalign;


import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * VectorCompatAlign commandline tool
 */
public class Main {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static final String OPTION_FROM = "s";
    private static final String OPTION_TO = "e";
    private static final String OPTION_HELP = "h";
    private static final String OPTION_VERSION = "v";

    private static final String VERSION = "0.1";
    private static final String NAME = "VectAlign";


    public static void main(String args[]) throws IOException {
        //TEST input args
        /*
            String star = "M 48,54 L 31,42 15,54 21,35 6,23 25,23 25,23 25,23 25,23 32,4 40,23 58,23 42,35 z";
            String arrow = "M 12, 4 L 10.59,5.41 L 16.17,11 L 18.99,11 L 12,4 z M 4, 11 L 4, 13 L 18.99, 13 L 20, 12 L 18.99, 11 L 4, 11 z M 12,20 L 10.59, 18.59 L 16.17, 13 L 18.99, 13 L 12, 20z";

            //args = new String[]{"-s", star, "--end", arrow};
            //args = new String[]{"-h"};
        */

        String fromSequence = null;
        String toSequence = null;

        try {
            CommandLineParser parser = new DefaultParser();
            Options options = initCommandLineOptions();
            CommandLine commandLine = parser.parse(options, args);

            if(commandLine.hasOption(OPTION_HELP)){
                String header = "\nAlign VectorDrawable sequences in order to allow morphing animations between them\n\n";
                String footer = "\nPlease report issues at https://github.com/bonnyfone/vectalign";
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar vectalign.jar ", header, options, footer, true);
                return;
            }
            else if(commandLine.hasOption(OPTION_VERSION)){
                System.out.println(NAME + " v"+VERSION);
                return;
            }

            File tmpFile;
            if(commandLine.hasOption(OPTION_FROM)){
                fromSequence = commandLine.getOptionValue(OPTION_FROM);

                tmpFile = new File(fromSequence);
                if(tmpFile.isFile() && tmpFile.exists())
                    fromSequence = readSequenceFromFile(tmpFile);
            }

            if(commandLine.hasOption(OPTION_TO)){
                toSequence = commandLine.getOptionValue(OPTION_TO);

                tmpFile = new File(toSequence);
                if(tmpFile.isFile() && tmpFile.exists())
                    toSequence = readSequenceFromFile(tmpFile);
            }


            String[] align = VectAlign.align(fromSequence, toSequence, VectAlign.Mode.BASE);

            System.out.println("\n--------------------");
            System.out.println("  ALIGNMENT RESULT  ");
            System.out.println("-------------------- ");
            System.out.println("\n# new START sequence:  \n" + ANSI_BLUE + align[0] + ANSI_RESET);
            System.out.println("\n# new END sequence:  \n" + ANSI_BLUE +align[1] + ANSI_RESET);

            System.out.println("\nNow, substitute your original sequences with the new aligned ones in your Android project.\n");


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private static String readSequenceFromFile(File f){
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(f.toURI()));
            return new String(encoded, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create commandLine options
     * @return
     */
    private static Options initCommandLineOptions(){
        Options options = new Options();

        options.addOption(OptionBuilder.withLongOpt("start")
                .withDescription("The VectorDrawable sequence from which we start the morphing")
                .hasArg()
                .withArgName("SEQUENCE | FILE")
                .create(OPTION_FROM));

        options.addOption(OptionBuilder.withLongOpt("end")
                .withDescription("The VectorDrawable sequence which ends the morphing")
                .hasArg()
                .withArgName("SEQUENCE | FILE")
                .create(OPTION_TO));

        options.addOption(OptionBuilder.withLongOpt("version")
                .withDescription("Print the version of the application")
                .create('v'));

        options.addOption(OptionBuilder.withLongOpt("help").create(OPTION_HELP));


        return options;
    }


}
