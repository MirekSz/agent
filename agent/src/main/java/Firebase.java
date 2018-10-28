import java.io.FileInputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Firebase {

	public static void main(String[] args) throws Exception {
		FileInputStream serviceAccount = new FileInputStream(
				"C:\\Users\\Mirek\\git\\agent2\\agent\\src\\main\\resources\\apec-4145e-firebase-adminsdk-p04lr-34d8d6385b_write.json");

		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setDatabaseUrl("https://apec-4145e.firebaseio.com").build();
		FirebaseApp.initializeApp(options);

		// String string =
		// FirebaseAuth.getInstance().createCustomTokenAsync("miro").get();
		// System.out.println(string);

		DatabaseReference database = FirebaseDatabase.getInstance("https://apec-4145e.firebaseio.com").getReference();
		database.child("posts").addValueEventListener(new ValueEventListener() {

			@Override
			public void onDataChange(DataSnapshot snapshot) {
				System.out.println("Dodanie value" + snapshot.getChildrenCount() + " " + snapshot.getValue());

			}

			@Override
			public void onCancelled(DatabaseError error) {
				// TODO Auto-generated method stub

			}
		});
		database.child("posts").addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String prevChildName) {
				System.out.println("Dodanie " + dataSnapshot.getChildrenCount() + " " + dataSnapshot.getValue());
			}

			@Override
			public void onCancelled(DatabaseError arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				System.out.println(arg0.getValue());

			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				// TODO Auto-generated method stub

			}
		});
		Thread.sleep(10000);
		database.child("posts/" + 7).child("info").setValueAsync("jan" + System.currentTimeMillis());
		database.child("posts/" + 8).child("info").setValueAsync("jan" + System.currentTimeMillis());
		database.child("posts/" + 9).child("info").setValueAsync("jan" + System.currentTimeMillis());
		Thread.sleep(100000);
		// GoogleCredential googleCred =
		// GoogleCredential.fromStream(serviceAccount);
		// GoogleCredential scoped = googleCred.createScoped(Arrays.asList(
		// "https://www.googleapis.com/auth/firebase.database",
		// "https://www.googleapis.com/auth/userinfo.email"));
		// scoped.refreshToken();
		// String token = scoped.getAccessToken();
		// System.out.println(token);
	}

}
