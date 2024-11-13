package com.example.myapplication.administrator;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.Toast;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.myapplication.R;
import com.example.myapplication.organizer.AddEventActivity;
import com.example.myapplication.organizer.OrganizerMainActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Large test class to test the buttons in the AdminBrowseQRCodes activity
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminBrowseQRCodesTest {
    private FirebaseFirestore db;
    private StorageReference storageReference;
    final CountDownLatch latchOne = new CountDownLatch(1);
    final CountDownLatch latchTwo = new CountDownLatch(1);

    @Rule
    public ActivityScenarioRule<AdministratorMainActivity> scenario = new
            ActivityScenarioRule<AdministratorMainActivity>(AdministratorMainActivity.class);

    /**
     * Testing the back button to confirm it sends the user back to the Admin Main Activity
     */
    @Test
    public void testBackButton() {
        onView(withId(R.id.browseQRcodesBtn)).perform(click());
        // verify we've entered the appropriate view before going back
        onView(withText("QR CODES")).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).perform(click());
        // confirms we have left the events view
        onView(withText("QR CODES")).check(doesNotExist());
        onView(withId(R.id.browseHeader)).check(doesNotExist());
        //confirms we have arrived back at the administrator main activity
        onView(withId(R.id.welcomeTextView)).check(matches(isDisplayed()));
    }

    /**
     * Testing the selection of a QRCode with the intent to delete
     */
    @Test
    public void testSelectFacility() {
        onView(withId(R.id.browseQRcodesBtn)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.contentListView)).atPosition(0).perform(longClick());
        onView(withText("Cancel")).check(matches(isDisplayed()));
        onView(withText("Delete")).check(matches(isDisplayed()));
        onView(withText("Delete QRCode?")).check(matches(isDisplayed()));
    }

    /**
     * testing the long click to delete a qrcode
     */
    @Test
    public void testDeleteQRCodeDelete() throws InterruptedException {
        setTestItems();
        latchOne.await();
        latchTwo.await();
        onView(withId(R.id.browseQRcodesBtn)).perform(click());
        onView(withText("invalidName")).check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.contentListView)).atPosition(0).perform(longClick());
        onView(withText("Delete")).perform(click());
        onView(withText("Cancel")).check(doesNotExist());
        onView(withText("Delete")).check(doesNotExist());
        onView(withText("Delete QRCode?")).check(doesNotExist());
        onView(withText("invalidName")).check(doesNotExist());
        deleteEvent();
    }

    /**
     * Testing the cancel button when deleting a facility
     */
    @Test
    public void testDeleteQRCodeCancel() {
        onView(withId(R.id.browseQRcodesBtn)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.contentListView)).atPosition(0).perform(longClick());
        onView(withText("Cancel")).perform(click());
        onView(withText("Cancel")).check(doesNotExist());
        onView(withText("Delete")).check(doesNotExist());
        onView(withText("Delete QRCode?")).check(doesNotExist());
    }

    /**
     * setting QRCode and event to for testing delete
     */
    private void setTestItems() {
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("event_posters");
        Map<String, Object> event = new HashMap<>();
        event.put("name", "invalidName");
        db.collection("events").document("00000")
                .set(event)
                .addOnSuccessListener(documentReference -> {
                    String eventId = "00000";
                    Bitmap qrCode = generateQRCode(eventId);
                    if (qrCode != null) {
                        uploadQRCodeToStorage(eventId, qrCode);
                    }
                    latchOne.countDown();
                });
    }

    /**
     * This method generate the QR code for the event and link it to the event detail page
     * @param eventId the eventID relating to the QR code
     * @return QR code
     */
    private Bitmap generateQRCode(String eventId) {
        QRCodeWriter writer = new QRCodeWriter();
        String deepLinkUrl = "myapp://event?id=" + eventId;
        try {
            BitMatrix bitMatrix = writer.encode(deepLinkUrl, BarcodeFormat.QR_CODE, 500, 500);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method upload the generated qr code to database
     * @param eventId the eventID relating to the QR code
     * @param qrCodeBitmap the QR code
     */
    private void uploadQRCodeToStorage(String eventId, Bitmap qrCodeBitmap) {
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOut);
        byte[] data = byteArrayOut.toByteArray();
        StorageReference qrCodeRef = storageReference.child("qrcodes/" + eventId + "_qr.jpg");
        UploadTask uploadTask = qrCodeRef.putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
            saveQRCodeUrlToFirestore(eventId, uri.toString());
        }));
    }

    /**
     * saving QRCode to Firestore
     * @param eventId event id for qrcode
     * @param qrCodeUrl url for qrcode
     */
    private void saveQRCodeUrlToFirestore(String eventId, String qrCodeUrl) {
        db.collection("events").document(eventId)
                .update("qrCodeUrl", qrCodeUrl)
                .addOnCompleteListener(task -> {
                    latchTwo.countDown();
                });
    }

    /**
     * method to delete event that was used for testing
     */
    private void deleteEvent() {
        db.collection("events").document("00000").delete();
    }

}
