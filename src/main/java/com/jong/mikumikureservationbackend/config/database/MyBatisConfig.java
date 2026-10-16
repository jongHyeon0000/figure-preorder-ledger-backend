package com.jong.mikumikureservationbackend.config.database;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.jong.mikumikureservationbackend.mapper")
public class MyBatisConfig {
}
