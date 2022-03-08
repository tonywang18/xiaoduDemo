package com.gidisoft.paul.sdk.orm;

import java.util.ArrayList;

public interface UserinfoImpl {
    void insert(UserBean myBean);

    void update(String name, String password);

    void delete(String name);

    ArrayList<UserBean> query(String name);

    ArrayList<UserBean> queryAll();
}
