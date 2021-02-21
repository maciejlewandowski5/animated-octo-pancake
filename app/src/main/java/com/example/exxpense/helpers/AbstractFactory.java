package com.example.exxpense.helpers;


import android.support.v4.app.Fragment;

import java.io.Serializable;

public interface AbstractFactory {

    public Fragment newInstance(Serializable ...serializable);

}
