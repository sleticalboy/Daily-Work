package com.sleticalboy.sample.service;

import com.sleticalboy.sample.service.DataStruct;

interface ITest {

    void doWrite(in DataStruct data);

    void doRead(in String name, in boolean notify);
}
