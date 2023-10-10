package com.api.project.service;


import top.zzkweb.sdk.client.ApiClient;

public class test {
    public static void main(String[] args) {
        ApiClient apiClient = new ApiClient("36c18d46e7425e9083221a3aba523214","7fdb39bc8ce739695875cb44efdb9607");
        String randomPicture = apiClient.getRandomPicture();
        System.out.println(randomPicture);
        String hi = apiClient.hello("hi");
        System.out.println(hi);
    }
}
