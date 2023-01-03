package com.Avbodh.Portfolio.Qrservice;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import com.mongodb.client.MongoClients;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.awt.Robot;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collections;

@Service
public class QrCodeReader {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void startListening() throws IOException, NotFoundException, AWTException {
        while (true) {
            // Capture QR code image from camera or other source
            BufferedImage image = captureQrCodeImage();

            // Decode QR code image
            String qrCodeData = decodeQrCode(image);

            // Save QR code data to MongoDB
            saveQrCodeData(qrCodeData);
        }
    }

    private void saveQrCodeData(String qrCodeData) {
        Query query = new Query();
        query.addCriteria(Criteria.where("qrCodeData").is(qrCodeData));
        Update update = new Update();
        update.set("qrCodeData", qrCodeData);
        mongoTemplate.upsert(query, update, "qrCodeData");
    }

    private String decodeQrCode(BufferedImage image) throws NotFoundException {
        MultiFormatReader reader = new MultiFormatReader();
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        HybridBinarizer binarizer = new HybridBinarizer(source);
        reader.setHints(Collections.singletonMap(DecodeHintType.POSSIBLE_FORMATS,
                Collections.singletonList(BarcodeFormat.QR_CODE)));
        Result result = reader.decode(new BinaryBitmap(binarizer));
        return result.getText();
    }

    private BufferedImage captureQrCodeImage() throws AWTException {
        Robot robot = new Robot();
        Rectangle screen = new Rectangle(0, 0, 1920, 1080);
        BufferedImage image = robot.createScreenCapture(screen);
        return image;

    }

    public void readQrCode(String filePath) throws IOException, NotFoundException {
        String atlasConnectionString = "mongodb+srv://admin:uday%40Mongo02@cluster0.z88hefp.mongodb.net/?retryWrites=true&w=majority";
        MongoClient mongoClient = MongoClients.create(atlasConnectionString);
        this.mongoTemplate = new MongoTemplate(mongoClient, "qrCodeData");

        File folder = new File(filePath);

        FileFilter imageFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                String fileName = file.getName();
                return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
            }
        };
        File[] imageFiles = folder.listFiles(imageFilter);
        for (File imageFile : imageFiles) {
            BufferedImage image = ImageIO.read(imageFile);

            MultiFormatReader reader = new MultiFormatReader();
            BufferedImageLuminanceSource luminanceSource = new BufferedImageLuminanceSource(image);
            HybridBinarizer binarizer = new HybridBinarizer(luminanceSource);
            com.google.zxing.BinaryBitmap bitmap = new com.google.zxing.BinaryBitmap(binarizer);

            Result result = reader.decode(bitmap, null);
            String qrCodeData = result.getText();

            Query query = new Query();
            query.addCriteria(Criteria.where("qrCodeData").is(qrCodeData));
            Update update = new Update();
            update.set("qrCodeData", qrCodeData);
            mongoTemplate.upsert(query, update, QrCodeData.class);
        }
    }

    public static void main(String[] args) {
        /*
         * QrCodeReader qrCodeReader = new QrCodeReader();
         * try {
         * qrCodeReader.readQrCode("filepath");
         * } catch (IOException | NotFoundException e) {
         * e.printStackTrace();
         * }
         */

        QrCodeReader qrCodeReader = new QrCodeReader();
        try {
            qrCodeReader.startListening();
        } catch (IOException | NotFoundException | AWTException e) {
            e.printStackTrace();
        }

    }

}
