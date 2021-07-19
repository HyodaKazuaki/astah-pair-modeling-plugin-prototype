# astah-pair-modeling-plugin
Astah\*でペアモデリングを実現するためのプラグイン

## 注意
このプラグインは現在アルファ版で提供されており、安定版ではありません。
このプラグインによってAstah\*がクラッシュすることや、正常に動作しない可能性があります。
全ての図においてペアモデリングが行えるわけではありません。
現在の対応状況については[対応状況](#対応状況)をご覧ください。
ペアモデリングを行うには、同じバージョンのAstah\*に同じバージョンのプラグインをインストールしておく必要があります。

このプラグインではネットワークを経由して通信を行いますが、**通信内容は一切暗号化していません**。
今後通信内容は暗号化する予定ですが、現時点のプラグインを利用して機密性の高いプロジェクトなど、第三者に盗聴されてはいけないプロジェクトのペアモデリングは行わないでください。

## 動作環境
Astah\* Professional 6.7

## 使い方
### プラグインのセットアップ
ペアモデリングを始める前に、中継サーバ(MQTTブローカーサーバ)をプラグインに設定します。
1. Astah\* Professionalを開き、メニューから「ツール」を選択する。
2. ツールタブの中から「ペアモデリングプラグイン」にカーソルを合わせる。
3. 横に表示された一覧の中から「環境設定(,)」を選択する。
4. ブローカーサーバのアドレスとポート番号を入力し、「OK」を選択する。

以上でプラグインのセットアップが完了します。
入力内容をキャンセルしたい場合は「Cancel」を選択してください。
2回目以降はここで設定した情報を利用してブローカーサーバに接続するため、設定し直す必要はありません。
設定を変更したい場合は再度同じ方法でブローカーサーバのアドレスとポート番号を変更することができます。
ブローカーサーバのアドレスにはIPv4アドレスのほかに`localhost`が入力可能です。

### ペアモデリングを開始する
ペアモデリングを始める前に、[プラグインのセットアップ](#プラグインのセットアップ)を完了している必要があります。
このプラグインでペアモデリングを行うには、誰かがホストとしてルームを作成し、参加者はホストからアイコトバを教えてもらう必要があります。
ホストは接続してきた参加者に対してプロジェクトの同期を行います。そのため、コンピュータやネットワークの性能が悪いと他の参加者に影響を及ぼす場合があります。

#### ルームを作る
1. Astah\* Professionalを開き、「プロジェクトを新規に作成する」アイコンをクリックするか、メニューにある「ファイル」から「プロジェクトの新規作成」を選択する。
   [対応状況](#対応状況)に記載されている要素のみで構成されている既存プロジェクトを使ってペアモデリングすることも可能です。この場合、同期したいプロジェクトをAstah\* Professionalで開いてください。
2. メニューから「ツール」を選択する。
3. ツールタブの中から「ペアモデリングプラグイン」にカーソルを合わせる。
4. 横に表示された一覧の中から、「ルームを作る(C)」を選択する。
5. 表示されたダイアログに従い、アイコトバを参加者に伝える。

以上でブローカーサーバに接続され、他のAstah\*とペアモデリングを行う準備ができます。

#### ルームに入る
1. メニューから「ツール」を選択する。
2. ツールタブの中から「ペアモデリングプラグイン」にカーソルを合わせる。
3. 横に表示された一覧の中から「ルームに入る(J)」を選択する。
4. ペアモデリングのホストから伝えられたアイコトバを入力し、「OK」を押す。

接続とプロジェクトの同期が完了すると、他のAstah\*とペアモデリングを行うことができます。
すでにプロジェクトを開いている場合、プロジェクトは自動的に閉じられます。
ただし、プロジェクトを保存していない場合は、ルームに入ることができません。
プロジェクトを保存するか閉じてからやり直してください。

### ペアモデリングを終了する
ペアモデリングを終了するには、[ペアモデリングを開始する](#ペアモデリングを開始する)を行いペアモデリングを行っている必要があります。

1. メニューから「ツール」を選択する。
2. ツールタブの中から「ペアモデリングプラグイン」にカーソルを合わせる。
3. 横に表示された一覧の中から「ルームから抜ける」を選択する。

以上でプローカーサーバとの接続が解除され、ペアモデリングが終了します。

## 対応状況
現在、以下の図でペアモデリングを行うことができます。

* クラス図(一部のみ対応)
* マインドマップ図(一部のみ対応)
* ステートマシン図(一部のみ対応)

## License
This plugin is released under the MIT License.
For details, See [LICENSE](LICENSE).
