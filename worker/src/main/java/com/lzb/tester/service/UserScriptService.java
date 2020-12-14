package com.lzb.tester.service;

import com.lzb.tester.dto.UserScriptDTO;
import com.lzb.tester.entity.UserScript;

public interface UserScriptService extends ManageService<UserScriptDTO> {

    UserScript insert(UserScriptDTO dto);
}
