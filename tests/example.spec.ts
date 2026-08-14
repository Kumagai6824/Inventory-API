import { test, expect } from "@playwright/test";

test.describe("在庫管理ダッシュボード UIテスト", () => {
  test.beforeEach(async ({ page }) => {
    // Spring Bootが起動しているローカル環境のダッシュボードへアクセス
    await page.goto("http://localhost:8080/");
  });

  test("ページタイトルと主要な要素が表示されていること", async ({ page }) => {
    // タイトルの検証
    await expect(page).toHaveTitle("在庫管理システム");

    // 見出しの検証
    await expect(
      page.getByRole("heading", { name: "在庫・商品管理ダッシュボード" }),
    ).toBeVisible();
  });

  test("新規商品を登録し、アラートが表示されること", async ({ page }) => {
    // 1. 商品名入力欄にテキストを入力
    const productNameInput = page.getByPlaceholder("商品名 (30文字以内)");
    await productNameInput.fill("テスト商品A");

    // 2. 「登録」ボタンをクリック（ダイアログのハンドリングを設定）
    page.once("dialog", async (dialog) => {
      expect(dialog.message()).toContain("was successfully created");
      await dialog.dismiss();
    });

    // 登録ボタンをクリック
    await page.getByRole("button", { name: "登録" }).first().click();
  });

  test("商品一覧取得ボタンを押してデータが表示されること", async ({ page }) => {
    // exact: true を指定して「一覧更新」ボタンに完全一致させる
    await page.getByRole("button", { name: "一覧更新", exact: true }).click();

    // 商品リスト（#productList）要素内にリストアイテムが表示されるか確認
    const productList = page.locator("#productList");
    await expect(productList).toBeVisible();
  });
});
