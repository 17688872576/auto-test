package com.lzb.tester.service;

public interface ClientInfoService<ClientInfo> extends ManageService<ClientInfo> {
    ClientInfo findOneByname(String username);
}
