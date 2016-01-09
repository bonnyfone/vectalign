package com.bonnyfone.vectalign;


import com.bonnyfone.vectalign.viewer.SVGViewer;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

/**
 * VectorCompatAlign tool (MAIN)
 */
public class Main {

    //Commandline Options
    private static final String OPTION_FROM = "s";
    private static final String OPTION_TO = "e";
    private static final String OPTION_HELP = "h";
    private static final String OPTION_VERSION = "v";
    private static final String OPTION_MODE = "m";
    private static final String OPTION_GUI = "g";

    //Application infos
    public static final String VERSION = "0.2";
    public static final String NAME = "VectAlign";

    /**
     * VectAlign commandLine main
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        /*TEST input args */
            String star = "M 48,54 L 31,42 15,54 21,35 6,23 25,23 25,23 25,23 25,23 32,4 40,23 58,23 42,35 z";
            String arrow = "M 12, 4 L 10.59,5.41 L 16.17,11 L 18.99,11 L 12,4 z M 4, 11 L 4, 13 L 18.99, 13 L 20, 12 L 18.99, 11 L 4, 11 z M 12,20 L 10.59, 18.59 L 16.17, 13 L 18.99, 13 L 12, 20z";
            String triangle = "M 91.095527,384.35546 L 254.23312,110.62095 L 405.71803,386.1926 z";
            String polystar = "M 177.11729,247.88609 L 256.31153,96.745452 L 217.85024,262.98609 L 388.33899,255.99999 L 225.13973,305.81185 L 316.43424,449.96639 L 191.69628,333.53762 L 112.50204,484.67825 L 150.96333,318.43762 L -19.525418,325.42372 L 143.67384,275.61186 L 52.379325,131.45732 z";
            //args = new String[]{"-s", triangle, "--end", polystar, "-m", "linear"};
            //args = new String[]{"-h"};

        String fromSequence = null;
        String toSequence = null;
        VectAlign.Mode mode = VectAlign.Mode.BASE;
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
            else if(commandLine.hasOption(OPTION_GUI)){
                SVGViewer.startVectAlignGUI();
                return;
            }

            if(commandLine.hasOption(OPTION_MODE)){
                mode = VectAlign.Mode.valueOf(commandLine.getOptionValue(OPTION_MODE).toUpperCase());
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

            if(fromSequence == null || toSequence == null){
                if(fromSequence == null)
                    System.out.println("Missing START path sequence. Specify the starting path using -s (or --start)");
                else
                    System.out.println("Missing END path sequence. Specify the ending path using -e (or --end)");

                return;
            }

            String[] align = null;
            try{
                align = VectAlign.align(fromSequence, toSequence, mode);
            }
            catch (Exception e){
                System.out.println("###################### EXCEPTION #####################");
                e.printStackTrace();
                System.out.println("######################################################");
                System.out.println("\nFor contributions or issues reporting, please visit " + Utils.ANSI_CYAN + "https://github.com/bonnyfone/vectalign \n "+ Utils.ANSI_RESET);
            }

            if(align == null){
                //Something went wrong, read exceptions!
                return;
            }

            System.out.println("\n--------------------");
            System.out.println("  ALIGNMENT RESULT  ");
            System.out.println("-------------------- ");
            System.out.println("\n# new START path:  \n" + Utils.ANSI_GREEN + align[0] + Utils.ANSI_RESET);
            System.out.println("\n# new END path:  \n" + Utils.ANSI_YELLOW +align[1] + Utils.ANSI_RESET);
            System.out.println("\nThese sequences are morphable and can be used as 'pathData' attributes inside of VectorDrawable files.\n");

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
        String header = "\nAlign two VectorDrawable paths in order to allow morphing animations between them.\n\n";
        String footer = "\nFor contributions or issues reporting, please visit"+ Utils.ANSI_CYAN + " https://github.com/bonnyfone/vectalign \n "+ Utils.ANSI_RESET;
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

        options.addOption(OptionBuilder.withLongOpt("gui")
                .withDescription("Start VectAlign GUI")
                .create(OPTION_GUI));

        options.addOption(OptionBuilder.withLongOpt("start")
                .withDescription("Starting VectorDrawable path (\"string\", txt file or SVG file)")
                .hasArg()
                .withArgName("\"string\"|txt_file|svg_file")
                .create(OPTION_FROM));

        options.addOption(OptionBuilder.withLongOpt("end")
                .withDescription("Ending VectorDrawable path (\"string\", txt file or SVG file)")
                .hasArg()
                .withArgName("\"string\"|txt_file|svg_file")
                .create(OPTION_TO));

        options.addOption(OptionBuilder.withLongOpt("mode")
                .withDescription("Aligning technique (default is BASE)")
                .hasArg()
                .withArgName("BASE|LINEAR|SUB_BASE|SUB_LINEAR")
                .create(OPTION_MODE));

        options.addOption(OptionBuilder.withLongOpt("version")
                .withDescription("Print the version of the application")
                .create(OPTION_VERSION));

        options.addOption(OptionBuilder.withLongOpt("help").create(OPTION_HELP));


        return options;
    }


}
