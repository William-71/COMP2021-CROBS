package hk.edu.polyu.comp.comp2021.crobs;

import hk.edu.polyu.comp.comp2021.crobs.model.CROBS;

/**
 * Crobs CLI entry point.
 * Launch example:
 * {@code java hk.edu.polyu.comp.comp2021.crobs.Application -html d:\log.html -txt d:\log.txt}
 */
public class Application {

    /**
     * @param args -html and -txt log paths
     */
    public static void main(String[] args) {
        String htmlPath = null;
        String txtPath = null;

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if ("-html".equals(args[i]) && i + 1 < args.length) {
                    htmlPath = args[++i];
                } else if ("-txt".equals(args[i]) && i + 1 < args.length) {
                    txtPath = args[++i];
                }
            }
        }

        CROBS crobs = new CROBS(htmlPath, txtPath);
        crobs.run(System.in, System.out);
    }
}
