package com.sleticalboy.objectbox.bean;

import java.io.Serializable;

import io.objectbox.annotation.BaseEntity;
import io.objectbox.annotation.Id;

@BaseEntity
public abstract class BaseBean implements Serializable {

    @Id
    public long dbId;
}
