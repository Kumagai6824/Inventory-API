# Inventory-API

このAPIはRaiseTech第9回課題をベースに作成中です。

## 使用した主な技術・ツール

<!-- PROJECT LOGO -->
[![Java][Java]][Java-url]
[![Spring][Spring]][Spring-url]
[![SpringBoot][SpringBoot]][SpringBoot-url]
[![Docker][Docker]][Docker-url]
[![MySQL][MySQL]][MySQL-url]
[![AWS][AWS]][AWS-url]
[![IntelliJ][IntelliJ]][IntelliJ-url]

<!-- MARKDOWN LINKS & IMAGES -->

[Java]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white

[Java-url]: https://getbootstrap.com

[Spring]: https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white

[Spring-url]: https://reactjs.org/

[SpringBoot]: https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=Spring&logoColor=white

[SpringBoot-url]: https://laravel.com

[Docker]: https://img.shields.io/badge/-Docker-EEE.svg?logo=docker&style=for-the-badge

[Docker-url]: https://angular.io/

[MySQL]: https://img.shields.io/badge/-MySQL-4479A1?style=for-the-badge&logo=mysql&labelColor=4479A1&logoColor=FFF

[MySQL-url]: https://jquery.com

[AWS]: https://img.shields.io/badge/Amazon_AWS-232F3E?style=for-the-badge&logo=amazon-web-services&logoColor=white

[AWS-url]: https://vuejs.org/

[IntelliJ]: https://img.shields.io/badge/Intellij%20Idea-000?logo=intellij-idea&style=for-the-badge

[IntelliJ-url]: https://svelte.dev/

## API概要

製品在庫を管理するAPIです。

- 商品情報（商品ID、商品名）のCRUD
- 在庫情報（在庫ID、商品ID、数量、履歴日付）のCRUD

## 作成背景

- シンプルな構成で、活用しやすいAPIとしたい
- 在庫管理という基礎的なシステムの開発を通じてCRUDを備えたRestAPI開発の学習、理解につなげたい

## API仕様書

[Inventory-API 仕様書](https://kumagai6824.github.io/Inventory-API/swagger/)

## クラス図（エンティティ部分）

```mermaid
classDiagram
        class Product {
            -int id
            -String name
            -DateTime deletedAt
        }

        note for InventoryProduct "Receiving/Shipping Info itself"
        class InventoryProduct {
            -int id
            -int productId
            -int quantity
            -DateTime history
        }

        note for InventoryHistory "Managing every histories from InventoryProducts per Products"
        class InventoryHistory {
            -int id
            -int productId
            -String name
            -int quantity
            -DateTime history
        }

        note for Inventory "Inventory for each Products"
        class Inventory {
            -int productId
            -String name
            -int quantity
        }

    Product "1..*" --> "0..*" InventoryProduct : has
    InventoryProduct "1..*" -- "1..*" InventoryHistory
    Product "1..*" -- "0..*" InventoryHistory
    Product "1..*" -- "0..*" Inventory
    InventoryProduct "1..*" -- "1..*" Inventory

    style Product stroke:#6f6
    style InventoryProduct stroke:#6f6
    style InventoryHistory stroke:#6f6
    style Inventory stroke:#6f6
```

## E-R図

![ERD](images/ERD.png)

## 環境変数

|変数名|役割|デフォルト値|
|----|----|----|
|SPRING_DATASOURCE_URL|MySQLのURL|jdbc:mysql://localhost:3308/inventory_database|
|SPRING_DATASOURCE_USERNAME|MySQLのユーザ名|user|
|SPRING_DATASOURCE_PASSWORD|MySQLのパスワード|password|

### AWS構成図

![AWS diagram](images/awsdiagram.svg)

## APIの機能紹介

### 商品情報の取り扱い

#### 1.商品情報の取得

登録済みの商品を全件取得します。

![GET PRODUCTS gif](images/get-products.gif)

#### 2.商品情報の登録

新たに商品を登録します。

![CREATE PRODUCTS gif](images/create-product.gif)

#### 3.商品IDでの商品情報の取得

指定した商品IDで該当の商品情報を取得します。

![GET PRODUCTS BYID gif](images/get-product-byid.gif)

#### 4.商品IDでの商品名の更新

指定した商品IDの商品名を更新します。

![UPDATE PRODUCTS gif](images/update-product.gif)

#### 5.商品IDでの商品名の削除

指定した商品IDの商品名を削除します。

![DELETE PRODUCT gif](images/delete-product.gif)

### 在庫情報の取り扱い

#### 6.現在庫の取得

現在庫を全て取得します。

![CURRENT INVENTORY gif](images/current-inventory.gif)

#### 7.入庫処理

商品を入庫します。

![RECEIVING INVENTORY gif](images/create-receiving.gif)

#### 8.在庫履歴の取得

指定した商品IDの在庫履歴を取得します。

![HISTORY gif](images/history.gif)

#### 9.入庫処理の修正

最後に実施した入庫処理に限り入庫数を修正できます。

![UPDATE RECEIVING gif](images/update-receiving.gif)

#### 10.出庫処理

商品を出庫します。

![SHIPPING INVENTORY gif](images/create-shipping.gif)

#### 11.出庫処理の修正

最後に実施した出庫処理に限り出庫数を修正できます。
在庫数を超える出庫数への修正は出来ません。

![UPDATE SHIPPING gif](images/update-shipping.gif)

#### 12.在庫情報の削除

最後に登録された在庫情報に限り、在庫IDを指定して削除できます。

![DELETE INVENTORY gif](images/delete-inventory.gif)


