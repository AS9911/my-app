# my-app
フロント：React　サーバー：SpringBoot　での開発です。

# git
GitHubでリモートリポジトリに開発用ブランチ作成
↓
git pull origin リモートリポジトリの最新情報取得
↓
git switch 作成したブランチ名　ローカルリポジトリ上で作成したブランチに切り替え
↓
git brach ローカルリポジトリ上で現在のブランチを確認

# React + TypeScript開発環境構築
① ルートディレクトリへ移動
例
cd my-app
② React + TypeScript プロジェクトを作成
Vite を使う場合
npm create vite@latest frontend -- --template react-ts

★起動★
cd frontend
npm run dev

# SpringBoot開発環境構築
① Java と VS Code の拡張機能を準備
・JDKのインストール
java -version でJDKの有無を確認⇒バージョンが表示されない場合、インストール※今回は不要

・VS Codeの拡張機能
以下をインストールします。

Extension Pack for Java
Spring Boot Extension Pack
Maven for Java

② Spring Initializrでプロジェクトを作成
VS Codeで
Ctrl + Shift + P
↓
Spring Initializr: Create a Maven Project

を選択します。

Spring Boot
3.5.x
Language
Java
Group
com.akiyama
Artifact
backend
Packaging
Jar
Java
21
③ Dependenciesを選択

最初は次のものがおすすめです。

必須
Spring Web
Spring Data JPA
Validation
PostgreSQL Driver
Lombok
後から追加してもよい
Spring Security
Spring Boot DevTools
Actuator
MyBatisFramework

★DB関係のドライバーを依存に含めていると未設定でビルド出来ない★
src\main\resources\application.propertiesに下記のような設定を追加

spring.datasource.url=jdbc:postgresql://localhost:5432/my_app
spring.datasource.username=admin
spring.datasource.password=8b2brksk1582SQL

★起動★
cd backend
.\mvnw.cmd spring-boot:run

・docker起動コマンド
docker compose up --build

・docker停止コマンド
docker compose stop