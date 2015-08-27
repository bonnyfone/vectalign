package com.bonnyfone.vectalign;


import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

/**
 * VectorCompatAlign commandline tool
 */
public class Main {

    //Commandline Options
    private static final String OPTION_FROM = "s";
    private static final String OPTION_TO = "e";
    private static final String OPTION_HELP = "h";
    private static final String OPTION_VERSION = "v";

    //Application infos
    private static final String VERSION = "0.1";
    private static final String NAME = "VectAlign";


    /**
     * VectAlign commandLine main
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        /*TEST input args
            String star = "M 48,54 L 31,42 15,54 21,35 6,23 25,23 25,23 25,23 25,23 32,4 40,23 58,23 42,35 z";
            String arrow = "M 12, 4 L 10.59,5.41 L 16.17,11 L 18.99,11 L 12,4 z M 4, 11 L 4, 13 L 18.99, 13 L 20, 12 L 18.99, 11 L 4, 11 z M 12,20 L 10.59, 18.59 L 16.17, 13 L 18.99, 13 L 12, 20z";

            //args = new String[]{"-s", star, "--end", arrow};
            //args = new String[]{"-h"};
        */

        String fromSequence = null;
        String toSequence = null;
        Options options = initCommandLineOptions();

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine commandLine = parser.parse(options, args);

            if(commandLine.getOptions()== null || commandLine.getOptions().length == 0 || commandLine.hasOption(OPTION_HELP)){
                printHelp(options);
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
                if(tmpFile.isFile() && tmpFile.exists()){
                    if(SVGParser.isSVGImage(tmpFile))
                        fromSequence = SVGParser.getPathDataFromSVGFile(tmpFile);
                    else
                        fromSequence = Utils.readSequenceFromFile(tmpFile);
                }
            }

            if(commandLine.hasOption(OPTION_TO)){
                toSequence = commandLine.getOptionValue(OPTION_TO);

                tmpFile = new File(toSequence);
                if(tmpFile.isFile() && tmpFile.exists()){
                    if(SVGParser.isSVGImage(tmpFile))
                        toSequence = SVGParser.getPathDataFromSVGFile(tmpFile);
                    else
                        toSequence = Utils.readSequenceFromFile(tmpFile);
                }
            }


            String[] align = VectAlign.align(fromSequence, toSequence, VectAlign.Mode.BASE);
            if(align == null){
                //Something went wrong, read exceptions!
                return;
            }

            System.out.println("\n--------------------");
            System.out.println("  ALIGNMENT RESULT  ");
            System.out.println("-------------------- ");
            System.out.println("\n# new START sequence:  \n" + Utils.ANSI_BLUE + align[0] + Utils.ANSI_RESET);
            System.out.println("\n# new END sequence:  \n" + Utils.ANSI_BLUE +align[1] + Utils.ANSI_RESET);
            System.out.println("\nNow, substitute your original sequences with the new aligned ones in your Android project.\n");

        } catch (ParseException e) {
            System.out.println("Wrong parameters!\n");
            printHelp(options);
        }

    }


    /**
     * Print the help message
     * @param options
     */
    private static void printHelp(Options options){
        String header = "\nAlign two VectorDrawable sequences in order to allow morphing animations between them.\n\n";
        String footer = "\nFor contributions or issues reporting, please visit https://github.com/bonnyfone/vectalign";
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(new Comparator<Option>() {
            @Override
            public int compare(Option o1, Option o2) {
                if(o1.hasArgs())
                    return 1;
                else if(o2.hasArgs())
                    return -1;

                return 0;
            }
        });
        formatter.printHelp("java -jar vectalign.jar ", header, options, footer, true);
    }


    /**
     * Create commandLine options
     * @return
     */
    private static Options initCommandLineOptions(){
        Options options = new Options();

        options.addOption(OptionBuilder.withLongOpt("start")
                .withDescription("VectorDrawable sequence (or SVG file) which represents the starting image")
                .hasArg()
                .withArgName("SEQUENCE | TXT_FILE | SVG_FILE")
                .create(OPTION_FROM));

        options.addOption(OptionBuilder.withLongOpt("end")
                .withDescription("VectorDrawable sequence (or SVG file) which represents the ending image")
                .hasArg()
                .withArgName("SEQUENCE | TXT_FILE | SVG_FILE")
                .create(OPTION_TO));

        options.addOption(OptionBuilder.withLongOpt("version")
                .withDescription("Print the version of the application")
                .create('v'));

        options.addOption(OptionBuilder.withLongOpt("help").create(OPTION_HELP));


        return options;
    }


}
