package com.recordtv.emd_video_play;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.recordtv.emd_video_play.helper.helper_pdf;

import java.io.File;
import java.io.FileOutputStream;

public class Pdf_Ganarate extends AppCompatActivity {
    private SharedPreferences sharedPref;
    EditText editText;
    FloatingActionButton btnSubmit;
    private View rootView;

    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf__ganarate);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        editText = (EditText)findViewById(R.id.editText);

        helper_pdf.pdf_textField(Pdf_Ganarate.this, rootView);

        btnSubmit = (FloatingActionButton)findViewById(R.id.fab);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String paragraph = editText.getText().toString().trim();
                if (paragraph.isEmpty()) {
                    Snackbar.make(editText, getString(R.string.toast_noText), Snackbar.LENGTH_LONG).show();
                } else {

                    File pdfFile = new File(helper_pdf.actualPath(Pdf_Ganarate.this));

                    if (pdfFile.exists()) {
                        title = sharedPref.getString("title", null);

                        helper_pdf.pdf_backup(Pdf_Ganarate.this);
                        createPDF();
                        helper_pdf.pdf_mergePDF(Pdf_Ganarate.this, editText);
                        helper_pdf.pdf_success(Pdf_Ganarate.this, editText);
                        helper_pdf.pdf_deleteTemp_1(Pdf_Ganarate.this);
                        helper_pdf.pdf_deleteTemp_2(Pdf_Ganarate.this);
                        editText.setText("");
                        helper_pdf.pdf_textField(Pdf_Ganarate.this, rootView);
                        helper_pdf.toolbar(Pdf_Ganarate.this);
                    } else {
                        Snackbar.make(editText, getString(R.string.toast_noPDF), Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    private void createPDF() {
        // Output file
        String outputPath = Environment.getExternalStorageDirectory() +  "/" + "123456.pdf";
        // Run conversion
        final boolean result = convertToPdf(outputPath);
        // Notify the UI
        if (result) {
            helper_pdf.pdf_success(Pdf_Ganarate.this, editText);
        } else Snackbar.make(editText, getString(R.string.toast_successfully_not), Snackbar.LENGTH_LONG).show();
    }


    private boolean convertToPdf(String outputPdfPath) {
        try {

            String paragraph = editText.getText().toString().trim();

            // Create output file if needed
            File outputFile = new File(outputPdfPath);
            if (!outputFile.exists()) outputFile.createNewFile();

            Document document;
            if (sharedPref.getString ("rotateString", "portrait").equals("portrait")) {
                document = new Document(PageSize.A4);
            } else {
                document = new Document(PageSize.A4.rotate());
            }

            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();
            document.add (new Paragraph(paragraph));

            document.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
