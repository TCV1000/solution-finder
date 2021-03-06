============================================================
コマンド: spin
============================================================

概要
============================================================

ある地形からTスピンできる置き方を探す

基本コマンド
============================================================

``java -jar sfinder.jar spin --tetfu v115@zgD8FeE8EeF8DeG8CeH8LeAgH --patterns *p7 -ft 4``

オプション一覧
============================================================

======== ====================== ======================
short    long                   default
======== ====================== ======================
``-t``   ``--tetfu``            なし
``-P``   ``--page``             1
``-p``   ``--patterns``         なし
``-fb``  ``--fill-bottom``      0
``-ft``  ``--fill-top``         -1
``-m``   ``--margin-height``    -1
``-c``   ``--line``             2
``-r``   ``--roof``             yes
``-mr``  ``--max-roof``         -1
``-s``   ``--split``            no
``-o``   ``--output-base``      output/path.txt
``-fp``  ``--field-path``       input/field.txt
``-pp``  ``--patterns-path``    input/patterns.txt
``-lp``  ``--log-path``         output/last_output.txt
======== ====================== ======================


出力結果
============================================================

[X] Z-Right L-Right I-Left T-Right [clear=4, hole=1, piece=4]

* 先頭のマーク
    * `O` = 実際にTスピンできる
    * `X` = Tミノを除いた地形を組むことができるが、Tミノの回転入れができない
    * `-` = Tミノを除いた地形が組めない

* ミノの方向

* clear = Tスピン以外も含めて消去されたライン数
* hole = Tスピン後の地形の穴の数（横穴も含む）
* piece = その解で使われたミノ数


探索TIPS
============================================================

.. |option_001| image:: img/option_001.png
   :scale: 100

このコマンドは、設定値次第では探索にかなり時間がかかる可能性があります。
そこで設定値を、以下の大まかな傾向をもとに調整していただくことをオススメします。

|option_001|


埋めたいライン（`-fb` と `-ft` の間）に、ブロックのない空間が含まれすぎないようにする
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

ブロックがほとんど置かれていないラインを探索範囲に含めると、ミノの置きかたの選択肢が増えるため、実行時間も増えていきます。
実際に探索範囲を大きくしても、範囲に比例して実用性のない置き方の解も増えていくため、ほとんどのケースでメリットが少ないと思われます。
そのため、ある程度予測して、埋めたいラインを選択すると良いと思います。


マージンの高さ（`-m` の値）を高すぎないようにする
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Tスピンをつくるには屋根が必要になるため、Tより上にブロックを置かないといけないケースが存在します。
そのため、探索範囲の上の方（`-ft` 付近）にTミノを置く場合、その屋根をつけるためのスペースをマージンの高さで指定します。

しかし、Tスピン用の屋根をつけるとTミノが入らなくなる可能性があるため、さらに回転入れ用の屋根が必要になることもあります。
この回転入れ用の屋根をみつけるのは難しく、マージンの高さに比例して実行時間が増えていきます。

デフォルトでは `-ftの値 + 2` になっていますが、この値を大きくする場合はご注意ください。

屋根に利用できるミノ数（`-mr` の値）を小さくする
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

もしラインを埋めるのに必要なミノが少ないと、残りのミノはすべて屋根（＆足場）に使えることになります。
その結果、Tミノを回転入れするために必要な屋根の探索に時間がかかることになります。
しかしながら屋根のミノが多い地形は、探索時間がかかる割にミノ順の制限が厳しく、有用な解となりにくいです。
そのため、解が一部制限されてしまいますが、通常時は屋根のミノを減らすことをオススメします。

`-mr 1` と指定すると、1ミノ置いてTミノが回転入れできるようになる解までが表示されます。
`-mr 0` と指定すると、何もしなくても回転入れできる解だけが表示されます。

似たオプションとして `-r no` で屋根の探索をスキップでき、このオプションでも探索速度は早くなります。
`-mr` と異なり、屋根の探索をそのものをスキップするため、そのままでは組めない地形も多く含まれます。
その点について、ご注意ください。

現バージョンでは未対応の機能
============================================================

* 地形を削らないとTスピンできないケース `サンプル <http://tinyurl.com/y2anl6g3>`_
* ミノを順列で指定できるようにする
    * 今後、指定されたミノ順で、実際に組むことができる解のみ表示させたいです。
    * Tミノ以外のミノに屋根が必要なケースは、順列指定でのみ対応させる予定です。 `その理由 <http://fumen.zui.jp/?v115@EhD8AeC8CeC8AeD8AeD8BeG8JeAglIhglCeywCeglD?ewwDehlQeAg0lAUYHDBQDxRA1dE6B0XHDBQpjRA1d0KB3XH?DBQeJSA1dkRBiAAAAqgAtHeBtHeAtweAg0DBMYHDBwFhRA1?w2KB1XHDBQUHSA1dkRBCYHDBQBFSA1d0KBGY3JBj+ESAVi+?AB5XHDBQOHSA1Ae3B1X/TBZ0mAAqgAPHeBPHeAPFeDAgWCA?SLCAgWDAQLDAhWGAJeAg0GAtjVRAz3AAAEhD8CeA8CeC8Ce?B8AeD8BeG8JeAg0qAlP52BxpDfEToXOBlP62A1vDfETY9KB?lvs2ACqDfET4d3Blvs2ACmAAAIhglRpAeywCeglRpBewwDe?hlQeAg0lAUYHDBQDxRA1dE6B0XHDBQpjRA1d0KB3XHDBQeJ?SA1dkRBiAAAA5fRpHeRpDfxSAeSLDexSBeQLWeAg0aBlvs2?AkJEfETIH+Blvs2A00btAls7fClvs2A2HEfET4xRBlvs2AU?GEfETY85AlP52BUDEfEWUDVBlvs2AWJEfEVpHIBl/PVB4pD?fET4JwBlvs2A1iAAAkfglIeglIeglQawSHexSCfgWRpGegW?RpGehWQeAg0OBlvs2AkJEfETIH+Blvs2A0kitAlszVClvs2?A2HEfET4xRBlvs2AUGEfETY85AlP52BUDEfEWUDVBlvs2A0?EEfEVpHIBl/PVB4ZAAAqgAtHeBtHeAtweAglvhBAg0mBlPB?BC5sDfET45ABlvs2AWxDfETY85AlP52BUDEfEWUDVBlvs2A?WJEfETYhBClvs2ADIEfEZk0KBlvs2A2HEfEVpM6AlPiOBmJ?EfETY12BlPJVByyDfETYN6Blvs2AUeAAAAg0mBlvs2AVGEf?ET4p9Blvs2AVJEfETYO6Alvs2AwpDfEX2NEBlPREBQ0DfET?ofzBlvs2A2yDfET4BBClPhzBGIEfEV5Z3Blvs2A1yDfET4J?wBlvs2AUuDfE032RBlPhzB5xAAA>`_


``-t``, ``--tetfu`` [default: なし]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

フィールドやオプションなどを指定したテト譜データを指定する。

テト譜で探索条件を指定する場合は ``--tetfu v115@vhAAgH`` のように指定する。

v115のテト譜データにのみ対応。


``-P``, ``--page`` [default: 1]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

テト譜でロードするページを指定する。

ページを変更したい場合は ``--page 31`` のように指定する。


``-p``, ``--patterns`` [default: なし]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

探索したいミノ順を指定する。

最大で22個のミノまで指定できる。

なお、renコマンドでは複数のミノを指定する記号（`*`, `[]`）は使用できません。

パターンを変更したい場合は ``--pattern IOSZLJTIO`` のように指定します。


``-fb``, ``--fill-bottom`` [default: 0]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

揃えるラインの範囲のうち、一番下のラインを指定する。
y座標は、一番下の段を `0` とする。

もし `2` を指定すると、y=2より上のラインを揃える組み合わせを探索する。
（3段目/y=2 は対象になる）

ここで指定する範囲には、Tスピンにならないライン消去（Tミノを含まないライン消去）も含まれる。


``-ft``, ``--fill-top`` [default: -1]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

揃えるラインの範囲の高さを指定する。
y座標は、一番下の段を `0` とする。

もし `4` を指定すると、y=4より下のラインを揃える組み合わせを探索する。
（5段目/y=4 は対象にならない）

ここで指定する範囲には、Tスピンにならないライン消去（Tミノを含まないライン消去）も含まれる。

`-1` が指定されたときは、自動的に 最も高い位置にあるブロックの高さ+1 に設定される。


``-m``, ``--margin-height`` [default: -1]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

ミノを置ける範囲の高さを指定する。
`-1` が指定されたときは、自動的に `fill-top` + 2 が設定される。

もし `4` を指定すると、y=4より下にミノを置く組み合わせを探索する。
（5段目/y=4 にブロックを置かない）

この高さは、Tスピンに必要なブロックや回転入れするための屋根の探索に利用される。


``-c``, ``--line`` [default: 2]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Tスピン時に消すべき最小のライン数を指定する。

`2` を指定したとき、T-Spin Double/Triple を探索する。


``-r``, ``--roof`` [default: yes]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

このオプションが `yes` のとき、Tミノの回転入れに必要な屋根の探索を実行する。
`no` を指定した場合は、屋根の探索をスキップされ、そのままではTミノを入れられない地形も解として表示される。

もし、屋根なしでTミノをそのまま入れられる解が必要であれば `-r yes -mr 0` を指定する。


``-mr``, ``--max-roof`` [default: -1]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Tミノの回転入れに必要な屋根の探索で置くことができる最大のミノ数を指定する。
`-1` が指定されたとき、指定パターンの中で制限なく屋根を置いて探索する。
そのため、この値が大きいほど実行時間が長くなる。

もし `0` を指定した場合は、屋根が必要ない解が選択される。


``-s``, ``--split`` [default: no]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

出力フォーマットがlinkのときに出力されるテト譜で、手順を1ミノずつ分割するか指定する。

ただし ``yes`` で生成されるテト譜はあくまで置き場所を示すものであり、ページ順通りに置くとミノが空中に浮いたり、移動できない場所に置かれることもあります。

* yes: 1ページにつき1ミノずつ表示される形で出力
* no: すべてのミノが1ページに納まった形で出力


``-o``, ``--output-base`` [default: output/ren.html]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

出力結果を保存するファイルのパスを指定する。


``-lp``, ``--log-path`` [default: output/last_output.txt]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

実行時のログを保存するファイルのパスを指定する。


``-fp``, ``--field-path`` [default: input/field.txt]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

フィールドを定義するファイルのパスを指定する。


``-pp``, ``--patterns-path`` [default: input/patterns.txt]
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

探索の組み合わせパターンを定義するファイルのパスを指定する。

