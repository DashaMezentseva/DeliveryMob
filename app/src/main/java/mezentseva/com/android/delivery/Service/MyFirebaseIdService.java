package mezentseva.com.android.delivery.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import mezentseva.com.android.delivery.Common.Common;
import mezentseva.com.android.delivery.Model.Token;

public class MyFirebaseIdService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        if (Common.currentUser != null)
            updateTokenToFirebase(tokenRefreshed);

    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token token = new Token(tokenRefreshed,false); //false because thia token send from Client app
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}