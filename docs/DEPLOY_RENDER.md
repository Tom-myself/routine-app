# Render へのデプロイ手順

## 前提

- GitHub にこのリポジトリがプッシュされていること
- Render のアカウント（[render.com](https://render.com)）でログインできること

---

## 1. PostgreSQL データベースを用意する

1. [Render Dashboard](https://dashboard.render.com/) で **New** → **PostgreSQL**
2. 次のように設定する：
   - **Name**: 任意（例: `routine-db`）
   - **Database**: 任意（例: `routine_db`）
   - **User / Password**: 自動生成で OK（後で環境変数に使う）
   - **Region**: アプリと同じリージョンを推奨
3. **Create Database** で作成する
4. 作成後、**Connections** に表示される **Internal Database URL** を控える  
   形式: `postgres://USER:PASSWORD@HOST:5432/DATABASE`

---

## 2. Web Service（アプリ）を作成する

1. **New** → **Web Service**
2. このリポジトリ（GitHub 連携済みなら一覧から選択）を選ぶ
3. 次のように設定する：

| 項目 | 値 |
|------|-----|
| **Name** | 任意（例: `routine-app`） |
| **Region** | DB と同じリージョン推奨 |
| **Branch** | デプロイしたいブランチ（例: `main`） |
| **Runtime** | **Docker** ではなく **Native**（または **Maven** が選べる場合はそれ） |

### Build & Deploy

| 項目 | 値 |
|------|-----|
| **Build Command** | `mvn clean package -DskipTests` |
| **Start Command** | `java -jar target/routine-app-0.0.1-SNAPSHOT.jar` |

- **Root Directory**: リポジトリルートのまま（空で OK）
- **Docker** は使わない想定（上記の Maven ビルド＋jar 起動）

---

## 3. 環境変数を設定する

Web Service の **Environment** で次を追加する。

### 必須（PostgreSQL）

Internal Database URL  
`postgres://USER:PASSWORD@HOST:5432/DATABASE`  
を次のように分解して設定する：

| Key | 値の例（実際は DB の接続情報に合わせる） |
|-----|------------------------------------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://dpg-xxxx-a.oregon-postgres.render.com:5432/routine_db`（**Internal** のホスト名・DB 名を使う） |
| `SPRING_DATASOURCE_USERNAME` | DB のユーザー名 |
| `SPRING_DATASOURCE_PASSWORD` | DB のパスワード |

- **Internal Database URL** のホスト部分が `xxx.oregon-postgres.render.com` のような形式なので、  
  `SPRING_DATASOURCE_URL` は  
  `jdbc:postgresql://そのホスト:5432/データベース名`  
  にする。

### 推奨（プロファイル・ポート）

| Key | 値 |
|-----|-----|
| `SPRING_PROFILES_ACTIVE` | `render` |

- 未設定でも、上記 3 つの `SPRING_DATASOURCE_*` を設定すれば本番では動く想定です。  
  `render` を指定すると `application-render.yml` が読み込まれ、本番向けの設定（ログレベルなど）が適用されます。
- ポートは Render が `PORT` を自動で渡すため、アプリ側で `server.port=${PORT:8080}` を読むだけで対応しています。

---

## 4. データベースを Web Service に「リンク」する（任意）

- 同じ Render アカウント内で PostgreSQL を作成している場合、Web Service の **Environment** で **Add Environment Group** や **Link Database** からその DB を選べる場合がある。
- リンクすると、その DB 用の環境変数が自動で追加されることがあるが、名前が `DATABASE_URL` などになるため、**このアプリでは上記の `SPRING_DATASOURCE_*` を手動で設定する方法**を推奨する。

---

## 5. デプロイする

1. 上記を保存し、**Manual Deploy** → **Deploy latest commit** でデプロイを開始する（または PR マージなどで自動デプロイする）
2. **Logs** でビルド・起動ログを確認する
3. 画面上部の **URL**（例: `https://routine-app-xxxx.onrender.com`）にアクセスする
4. 新規登録 → ログイン → ルーティーン作成まで動作すれば OK

---

## トラブルシュート

- **ビルドが失敗する**  
  - **Build Command** が `mvn clean package -DskipTests` になっているか確認する  
  - ログで `command not found: mvn` の場合は、Render の **Runtime** が **Maven / Java** になっているか確認する
- **起動しない / 503**  
  - **Start Command** が `java -jar target/routine-app-0.0.1-SNAPSHOT.jar` のままか確認する  
  - `pom.xml` の `<artifactId>routine-app</artifactId>` と jar 名が一致しているか確認する
- **DB に接続できない**  
  - **Internal Database URL** を使っているか（同じ Render 内なら Internal が必須）  
  - `SPRING_DATASOURCE_URL` が `jdbc:postgresql://ホスト:5432/DB名` の形式か  
  - ユーザー名・パスワードに特殊文字が含まれる場合は Render の **Environment** でエスケープやクォートの要否を確認する

---

## まとめ（コピー用）

- **Build Command**: `mvn clean package -DskipTests`
- **Start Command**: `java -jar target/routine-app-0.0.1-SNAPSHOT.jar`
- **環境変数**:  
  `SPRING_DATASOURCE_URL` / `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` を PostgreSQL の接続情報に合わせて設定（任意で `SPRING_PROFILES_ACTIVE=render`）。
