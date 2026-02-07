# ルーティーン管理アプリ

1日の使える時間から逆算して、1週間分の勉強スケジュールを管理するWebアプリです。

## 技術スタック

- **Spring Boot 3.2** (Maven)
- **PostgreSQL**
- **Spring Data JPA**
- **Thymeleaf**
- **Spring Security**（認証）

## 機能（MVP）

- **ルーティーンのCRUD**
  - 一覧表示（1週間分を曜日別に表示）
  - 詳細表示
  - 新規作成
  - 編集
  - 削除
- **ユーザー認証**
  - 新規登録
  - ログイン / ログアウト

## セットアップ

### 前提

- Java 17
- Maven 3.6+

### 1. データベースの選び方

**Docker も PostgreSQL も入れていない場合（いちばん手軽）:**

H2 という組み込み DB を使います。追加のインストールは不要です。

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

データはプロジェクト直下の `data/` フォルダに保存されます（次回起動時も残ります）。

---

**Docker を使う場合（psql 不要・DB 自動作成）:**

```bash
docker run -d --name postgres-routine -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=routine_db -p 5432:5432 postgres:16
```

**PostgreSQL をローカルにインストールしている場合:**

```bash
createdb routine_db
```

### 2. 設定（PostgreSQL を使うときだけ）

`application.yml` のデフォルトは PostgreSQL です。H2 で動かす場合は上記のとおり `-Dspring-boot.run.profiles=local` を付けてください。PostgreSQL を使う場合は `src/main/resources/application.yml` の接続情報を環境に合わせて変更してください。

### 3. 起動

**H2 で動かす場合:**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**PostgreSQL で動かす場合:**

```bash
mvn spring-boot:run
```

（Maven が未インストールの場合は、IDE の Spring Boot 実行や [start.spring.io](https://start.spring.io/) で Maven Wrapper を追加したプロジェクトを生成してから `./mvnw spring-boot:run` も利用できます。）

ブラウザで http://localhost:8080 にアクセスします。

### 4. 初回利用

1. 「新規登録」からアカウントを作成
2. ログイン後、「1週間のスケジュール」または「新規作成」でルーティーンを追加
3. 曜日・開始時刻・所要時間（分）を入力して保存

## プロジェクト構成

```
src/main/java/com/example/routineapp/
├── config/          # Security, WebMvc 設定
├── controller/      # 認証・ルーティーン Controller
├── dto/             # RoutineForm など
├── entity/          # User, Routine
├── repository/      # JPA Repository
└── service/         # ビジネスロジック
```

## デプロイ（Render）

[Render](https://render.com) へのデプロイ手順（Build/Start コマンド・環境変数）は [docs/DEPLOY_RENDER.md](docs/DEPLOY_RENDER.md) を参照してください。

## ライセンス

MIT
