package com.maestral.pack.packapp.API;

import com.maestral.pack.packapp.models.Member;

import java.util.List;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by irfanka on 4/17/16.
 */
public interface PackApi {
    @GET("Groups/GetAllGroupMembers")
    Call<List<Member>> getMembers();

    @POST("Members")
    Call<Member> createMember(@Body Member member);

    @POST("Groups/AddMemberToGroup")
    Call<String> AddMemberToGroup(@Body Member member);

    @PUT("Groups/UpdateMemberGeoLocation/{username}")
    Call<String> updateLocation(@Body double[] location, @Path("username") String username);

    @PUT("Members/PutMemberIsPanicking/{username}")
    Call<String> updatePanic(@Body boolean isPanicking, @Path("username") String username);

    @POST("Groups/AddGroup/{groupName}")
    Call<String> AddGroup(@Body Member member, @Path("groupName") String groupName);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://172.18.1.172/PackWebApiServices/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
