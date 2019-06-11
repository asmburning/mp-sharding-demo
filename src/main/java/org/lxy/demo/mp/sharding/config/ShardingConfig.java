package org.lxy.demo.mp.sharding.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.shardingsphere.api.config.rule.ShardingRuleConfiguration;
import io.shardingsphere.api.config.rule.TableRuleConfiguration;
import io.shardingsphere.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.*;

/**
 * @author liuxinyi
 * @date 2019-06-11
 */
@Configuration
public class ShardingConfig {


    @Bean("hikariDataSource")
    public HikariDataSource ds() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/mp");
        config.setMinimumIdle(10);
        config.setMaximumPoolSize(20);
        config.setUsername("bart");
        config.setPassword("51mp50n");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }

    @Bean("shardingDataSource")
    DataSource shardingDataSource() throws Exception {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getOrderTableRuleConfiguration());
        shardingRuleConfig.getBindingTableGroups().add("t_test, t_test_item");
        //shardingRuleConfig.getBroadcastTables().add("t_config");
        //shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "ds${user_id % 2}"));
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("guid", new IdWorkerMonthTableShardingStrategy()));
        return ShardingDataSourceFactory.createDataSource(createDataSourceMap(), shardingRuleConfig, new LinkedHashMap<>(), new Properties());
    }

    TableRuleConfiguration getOrderTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration();
        result.setLogicTable("t_test");
        result.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("guid", new IdWorkerMonthTableShardingStrategy()));
        //result.setActualDataNodes("ds${0..1}.t_order${0..1}");
        return result;
    }

    Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> result = new HashMap<>();
        result.put("ds0", ds());
        return result;
    }


    @Bean
    public GlobalConfig globalConfig() {
        // 全局配置文件
        GlobalConfig globalConfig = new GlobalConfig();
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        // 默认为自增
        dbConfig.setIdType(IdType.ID_WORKER_STR);
        // 手动指定db 的类型, 这里是mysql
        globalConfig.setDbConfig(dbConfig);
        // 逻辑删除注入器
        return globalConfig;
    }


    @Bean("mybatisSqlSessionFactoryBean")
    public MybatisSqlSessionFactoryBean sqlSessionFactoryBean(
            GlobalConfig globalConfig) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(shardingDataSource());
        sqlSessionFactoryBean.setGlobalConfig(globalConfig);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setGlobalConfig(globalConfig);
        configuration.setMapUnderscoreToCamelCase(true);
        sqlSessionFactoryBean.setConfiguration(configuration);
        List<Interceptor> interceptors = new ArrayList<>();
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置分页插件
        interceptors.add(paginationInterceptor);
        // 如果是dev环境,打印出sql, 设置sql拦截插件, prod环境不要使用, 会影响性能
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        interceptors.add(performanceInterceptor);
        sqlSessionFactoryBean.setPlugins(interceptors.toArray(new Interceptor[0]));
        return sqlSessionFactoryBean;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(MybatisSqlSessionFactoryBean sqlSessionFactoryBean) {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("org.lxy.demo.mp.sharding.mapper");
        // 设置为上面的 factory name
        configurer.setSqlSessionFactoryBeanName("mybatisSqlSessionFactoryBean");
        return configurer;
    }
}
