package com.akiyama.backend.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.akiyama.backend.domain.mapper")
public class MyBatisConfig {

    @Bean
    // MyBatisの「設定＋接続＋Mapper読み込み」をまとめて管理する中心クラス
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        // SqlSessionFactory ：SQLセッション（接続・マッピング設定の本体）
        // SqlSessionTemplate：実際にMapperを安全に使うためのラッパー
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        // DB接続情報（URL / ユーザー / パスワード）を渡す
        // これがないとSQL実行できない
        factory.setDataSource(dataSource);
        // Mapper XMLの読み込み
        // classpath*: → 複数モジュール対応（全部拾う）/ mapper/**/*.xml → 再帰的に全XMLを対象
        // ->resources/mapper/ 配下の全SQL定義XMLを自動ロード
        factory.setMapperLocations(
            // classpath上のファイル（特にXMLや設定ファイル）をまとめて探すためのクラス
            new org.springframework.core.io.support.PathMatchingResourcePatternResolver()
                    .getResources("classpath*:mapper/**/*.xml")
        );
        factory.setTypeAliasesPackage("com.akiyama.backend.domain.entity");
        // MyBatisの完成品（SqlSessionFactoryインスタンス）を取り出す処理
        return factory.getObject();
    }

    @Bean
    // MyBatisの「実行部分」をSpring管理に合わせたもの
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}