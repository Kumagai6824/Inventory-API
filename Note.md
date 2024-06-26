# Note

## 登録処理が完了して引数のユーザーと新しく採番されたIDが設定されること()

### エラー１

#### 実装内容

```
void 登録処理が完了して引数のユーザーと新しく採番されたIDが設定されること() {
        Name product = new Name();
        product.setName("Kumagai");
        productMapper.createName(product);
        assertNotEquals((Integer) null, product.getId());
        assertThat(productMapper.findById(1)).contains(new Name(1, "Kumagai"));
    }
```

#### エラー文

```
Expecting Optional to contain:
  com.raisetech.inventoryapi.entity.Product@4d480951
but was empty.
java.lang.AssertionError: 
Expecting Optional to contain:
  com.raisetech.inventoryapi.entity.Product@4d480951
but was empty.
```

* Name@4d480951が含まれるはずがemptyとのこと
* デバッグにてid=30と判明

#### 対応内容

* 自動生成されたidをリセット
    * DBのテーブルを削除して再作成する方法
    * 自動採番されたIDをリセットするSQLコマンドを実行する方法（今回はこれ）
* @Sqlにreset-id.sqlを追加し、id=1となるように変更した

#### 実行結果

* 一応テスト成功
