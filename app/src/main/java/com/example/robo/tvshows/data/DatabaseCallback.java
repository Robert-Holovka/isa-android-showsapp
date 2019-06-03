package com.example.robo.tvshows.data;

public interface DatabaseCallback<T> {

    void onSuccess(T data);
    void onError(Throwable t);
}