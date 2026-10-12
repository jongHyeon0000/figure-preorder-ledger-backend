package com.jong.figurepreorderledgerbackend.config.database;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.jong.figurepreorderledgerbackend.mapper")
public class MyBatisConfig {
}
