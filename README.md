# astah-pair-modeling-plugin
Astah*でペアモデリングを実現するためのプラグイン

## 注意
このプラグインは現在アルファ版で提供されており、安定版ではありません。
このプラグインによってAstah*がクラッシュすることや、正常に動作しない可能性があります。
全ての図においてペアモデリングが行えるわけではありません。
現在の対応状況については[対応状況](#対応状況)をご覧ください。

このプラグインではネットワークを経由して通信を行いますが、**通信内容は一切暗号化していません**。
今後通信内容は暗号化する予定ですが、現時点のプラグインを利用して機密性の高いプロジェクトなど、第三者に盗聴されてはいけないプロジェクトのペアモデリングは行わないでください。

## 動作環境
Astah* Professional 6.7

## 使い方
### プラグインのセットアップ
ペアモデリングを始める前に、中継サーバ(ブローカーサーバ)をプラグインに設定します。
1. Astah* Professionalを開き、メニューから「ツール」を選択する
2. ツールタブの中から「ペアモデリングプラグイン」にカーソルを合わせる
3. 横に表示された一覧の中から「環境設定(,)」を選択する
4. ブローカーサーバのアドレスを入力し、「OK」を選択する
5. ブローカーサーバのポート番号を入力し、「OK」を選択する

以上でプラグインのセットアップが完了します。
途中で入力をキャンセルしたい場合は「Cancel」を選択してください。
2回目以降はここで設定した情報を利用してブローカーサーバに接続するため、設定し直す必要はありません。
設定を変更したい場合は再度同じ方法でブローカーサーバのアドレスとポート番号を変更することができます。

### ペアモデリングを開始する
ペアモデリングを始める前に、[プラグインのセットアップ](#プラグインのセットアップ)を完了している必要があります。
1. Astah* Professionalを開き、「プロジェクトを新規に作成する」アイコンをクリックするか、メニューにある「ファイル」から「プロジェクトの新規作成」を選択する
2. メニューから「ツール」を選択する
3. ツールタブの中から「ペアモデリングプラグイン」にカーソルを合わせる
4. 横に表示された一覧の中から「ペアモデリングを始める(P)」を選択する

以上でブローカーサーバに接続され、他のAstah*とペアモデリングを行うことができます。

### ペアモデリングを終了する
ペアモデリングを終了するには、[ペアモデリングを開始する](#ペアモデリングを開始する)を行いペアモデリングを行っている必要があります。
1. Astah* Professionalを開き、「プロジェクトを新規に作成する」アイコンをクリックするか、メニューにある「ファイル」から「プロジェクトの新規作成」を選択する
2. メニューから「ツール」を選択する
3. ツールタブの中から「ペアモデリングプラグイン」にカーソルを合わせる
4. 横に表示された一覧の中から「ペアモデリングを始める(P)」を選択する

以上でプローカーサーバとの接続が解除され、ペアモデリングが終了します。

## 対応状況
現在、以下の図でペアモデリングを行うことができます。
* クラス図(一部のみ対応)
* マインドマップ図(一部のみ対応)
* ステートマシン図(一部のみ対応)

## License
This plugin released under the MIT License.
For details, See [LICENSE](LICENSE).
