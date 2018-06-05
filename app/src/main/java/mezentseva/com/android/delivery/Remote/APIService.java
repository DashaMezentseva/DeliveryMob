package mezentseva.com.android.delivery.Remote;

import mezentseva.com.android.delivery.Model.MyResponse;
import mezentseva.com.android.delivery.Model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAGM3oxEo:APA91bFlu_WFIjPPzrbbJnJlTG35SeinmPh7YrpUDemlLyfyEy3ku2TsXzbYwk9WY-IsMFVHs5L8K-irByNZ2Xzd2hTVB8sTZMJuPxCbZL9oUGylCeAMpFXcdGQRm77oQCbR0KrakMyq"
            }


    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
