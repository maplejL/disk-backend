package com.cslg.disk.example.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
public class LogServiceImpl implements LogService {

    @Override
    public boolean save(SysLog sysLog) {
        log.info(sysLog.getParams());
        return true;
    }
}
