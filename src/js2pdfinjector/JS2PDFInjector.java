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
 * This class behaves as follows:
 * 1) File Open Prompt to select PDF file to embed JS into.
 * 2) File Open Prompt to select .js file to embed.
 * 3) Prompt displaying successful execution or error message.
 * 
 * @author cornerpirate
 */
public class JS2PDFInjector {

    public static void main(String args[]) {

        try {

            JFileChooser fileChooser = new JFileChooser();
            // do popup to get the file or files for input
            fileChooser.setMultiSelectionEnabled(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            fileChooser.setFileFilter(filter);

            File pdf_in = null;
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                pdf_in = fileChooser.getSelectedFile();
            } else {
                JOptionPane.showMessageDialog(null, "You did not select a file");
                System.exit(-1);
            }

            String output_name = pdf_in.getParent() + File.separator + "js_injected_" + pdf_in.getName();
            System.out.println("output_name: " + output_name);
            File js_injected_pdf = new File(output_name);

            PDDocument document = PDDocument.load(pdf_in);
            System.out.println("File Opened: " + pdf_in);

            filter = new FileNameExtensionFilter("JavaScript File", "js");
            fileChooser.setFileFilter(filter);
            // clear PDF file from selected.
            fileChooser.setSelectedFile(new File(""));

            File js_in = null;
            returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                js_in = fileChooser.getSelectedFile();
            } else {
                JOptionPane.showMessageDialog(null, "You did not select a file");
                System.exit(-1);
            }

            System.out.println("File Opened: " + js_in);

            String content = new Scanner(js_in).useDelimiter( File.separator + "Z").next();

            PDActionJavaScript javascript = new PDActionJavaScript(content);

            document.getDocumentCatalog().setOpenAction(javascript);
            document.save(js_injected_pdf);

            System.out.println("File Saved: " + js_injected_pdf);

            JOptionPane.showMessageDialog(null, "Sucessfully injected at: " + js_injected_pdf);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex);
        }

    }
}

