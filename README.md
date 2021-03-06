# 3D-Packaging-Problem
<div id="article_content" class="article_content clearfix csdn-tracking-statistics" data-pid="blog" data-mod="popu_307" data-dsm="post">
								            <div id="content_views" class="markdown_views prism-atom-one-dark">
							<!-- flowchart 箭头图标 勿删 -->
							<svg xmlns="http://www.w3.org/2000/svg" style="display: none;"><path stroke-linecap="round" d="M5,0 0,2.5 5,5z" id="raphael-marker-block" style="-webkit-tap-highlight-color: rgba(0, 0, 0, 0);"></path></svg>

<p class="introduction">(一) 前言</p>

<p class="introduction">　　转。临时页面。 <br>
　　 <br>
(二) 背景及业务叙述</p>

<p class="introduction">　　顾客下完订单以后，零售商库房将商品打包出库；库房工作人员将订单所购买的商品装到零售商所提供的不同型号的箱子中，打包寄送给顾客。 <br>
　　顾客下的订单中包含各式各样的大小规格的商品，有一些不能倒置，像玻璃瓶的酒水饮料、易碎物品，带重力感应的仪器设备，都不可倒置。 <br>
供应商提供不同种类的箱型，以方便商品的二次包装。 <br>
　　装箱工人需要根据实际情况将不同类型的商品放置到合适的箱子中去。</p>

<p class="introduction">(三) 问题叙述</p>

<p class="introduction">　　首先，装箱工人在装箱过程中需要对订单的商品选择箱型，评估商品体积和箱子容积的正确比例，这一过程极其耗费资源，给仓库作业带来困难的同时还降低了作业效率。 <br>
　　其次，所送商品会出现装箱不合理的情况，如，顾客只是买一款体积很小的商品，收到的确是一个很大很空的箱子，浪费资源的同时，也给用户带来困惑，同时零售商的专业性也遭到了质疑。</p>

<p class="introduction">(四) 编写目的以及适用读者</p>

<p class="introduction">　　为了帮助作者实现算法过程中能够捋清思路，不迷失方向。</p>

<p class="introduction">(五) 需求（自己整理，如有偏差，见谅）</p>

<p class="introduction">1，自动计算出某一订单需要各种规格的箱子各多少个。 <br>
2，商品绝对不可以倒置（旋转180度、上下颠倒）。但是某些商品可以躺着放（旋转90度、横向放置）。 <br>
3，尽可能不浪费箱子资源，箱子尽可能装满。</p>

<p class="introduction">(六) 定义</p>

<p class="introduction">术语定义：</p>

<p class="introduction">1，  箱型：箱子的型号，记录记录箱子内部空间的长宽高等属性。箱型的长宽高特指箱体内部的长宽高。 <br>
2，  常规箱型：最常用的箱子型号，符合大多数商品，以及方便搬运的体积。 <br>
3，  特型：最不常用的箱型，不方便运输以及码放。例如：装晾衣杆的特长箱型、装大号电冰箱的特大号箱型。 <br>
4，  常规指数：箱型的orderNum属性，用来表示优先使用的箱子，和不推荐使用的特型箱子。（数字越大，越常规） <br>
5，  容积：特指箱子的容量。 <br>
6，  剩余容积：箱子装入一个商品后还剩多少空间，箱子的剩余空间用构造好的若干个更小的箱型表示。 <br>
7，  体积：特指商品的体积。（这里每个箱子只装一个商品，剩下的空间将分割成更小的3个箱子） <br>
8，  满：箱子中放不下订单中任何其他商品的时候，箱子状态为满。 <br>
9，  未满：箱子中还可以放下订单中的其他任意小件商品时候，箱子状态为未满。 <br>
10， 爆箱：箱子还有一部分空间，但是订单中最小的商品体积比箱子的剩余容积大。 <br>
11， 符合度：特指一组商品和某种箱型的符合度，数值越小，越符合。符合度公式：0-（剩余容积+容积–常规指数）得到的数字越大，符合度越高。</p>

<p class="introduction">规则定义：</p>

<p class="introduction">1，  所有箱子均为标准的长方体或者正方体。长宽高为内围长宽高（从箱子内部测量，如果是从箱子外部测量，需减去箱壁的厚度）。 <br>
2，  所有商品均描述为矩形，长宽高为外围长宽高。 <br>
3，  装箱时，优先放置大件商品。如果大件商品放不满的情况下，考虑次小商品，直到放满为止。 <br>
4，  装箱时，优先选择容积小的箱子。容积更小的箱子如果能够装下商品，则剩余容积会更小，说明箱子更适合商品。其次优先选择常规指数最大的箱子。（值越大越常规，值越小表示是非常规箱型甚至是特型箱） <br>
5，  如果订单中的商品恰好已经装完，箱子未满，尝试更换容积更小的箱子。如果爆箱则换回原来的箱型。 <br>
6，  如果订单中的商品恰好已经装完，出现爆箱，尝试更换小一号箱子，后继续装箱剩余商品。如果此时箱子已经是最小，则更换成多个箱子装箱。 <br>
7，  将箱型按照符合度值由大到小的顺序排列。如果箱子可以装满的情况下优先选择符合度值大的箱子。 <br>
8，  将箱型按照容积从小到大的顺序排列。如果箱子装不满优先尝试小的箱型。 <br>
9，  箱子优先放置大件商品，然后放置次大商品，最后放订单中最小的商品。 <br>
10， 优先尝试使用单个箱子完成一个订单的商品的包装，其次尝试使用多箱完成一个订单的商品包装。 <br>
11， 如果所有的箱子都不能一个箱子装下单个订单内的全部商品，才会启用多个箱子来装商品。（个别特性箱可能只正对某一款商品，此时该箱型可以不参与该逻辑运算。）</p>

<p class="introduction">(七) 数据模型</p>

<p class="introduction">实体关系ER <br>
<img src="https://img-blog.csdn.net/20160706105438410" alt="这里写图片描述" title=""></p>

<p class="introduction">实体清单 <br>
<img src="https://img-blog.csdn.net/20160706112849653" alt="这里写图片描述" title=""></p>

<p class="introduction">实体属性</p>

<ol>
<li><p class="introduction">订单 <br>
<img src="https://img-blog.csdn.net/20160706112947186" alt="这里写图片描述" title=""></p></li>
<li><p class="introduction">订单项 <br>
<img src="https://img-blog.csdn.net/20160706113048943" alt="这里写图片描述" title=""></p></li>
<li><p class="introduction">商品 <br>
<img src="https://img-blog.csdn.net/20160706113213532" alt="这里写图片描述" title=""></p></li>
<li><p class="introduction">箱子 <br>
<img src="https://img-blog.csdn.net/20160706113305018" alt="这里写图片描述" title=""></p></li>
<li><p class="introduction">箱型 <br>
<img src="https://img-blog.csdn.net/20160706113347112" alt="这里写图片描述" title=""></p></li>
<li><p class="introduction">订单的盒子清单项 <br>
<img src="https://img-blog.csdn.net/20160706113456254" alt="这里写图片描述" title=""></p></li>
<li><p class="introduction">客户 <br>
<img src="https://img-blog.csdn.net/20160706113606943" alt="这里写图片描述" title=""></p></li>
</ol>

<p class="introduction">(七) 实现过程</p>

<p class="introduction">单个商品的一次装箱过程</p>

<p class="introduction">商品的长宽高以及箱子的长宽高分别如下图所示： <br>
<img src="https://img-blog.csdn.net/20160706113746289" alt="这里写图片描述" title=""> <br>
　　　　　　　　　　　　　　　　　　　图一 <br>
单个商品装入箱子中以后如下图所示： <br>
<img src="https://img-blog.csdn.net/20160706113902649" alt="这里写图片描述" title=""></p>

<p class="introduction">装进箱子中以后所产生的剩余空间如下图所示：  <br>
<img src="https://img-blog.csdn.net/20160706114012007" alt="这里写图片描述" title=""></p>

<p class="introduction">　　在商品可以装入箱子的情况下，每装入一次，可以产生三块新的空间，也就是新的箱子。三个新的箱子的大小是可以通过计算得到的。</p>

<p class="introduction">　　假如上述方案，商品的长度、宽度或者高度中任意一个维度的值要大于箱子的长度、宽度或者高度的话，表示箱子装不下此商品，需要使用其它的摆放方案解决装箱问题。下文列出三种其它方案，加上上述方案，一共四种。</p>

<p class="introduction">其它摆放方案</p>

<p class="introduction">　　假设商品不可以倒置（其实商品在矩形形态下，倒置和正常放置占用空间一样，所以不考虑该情况），第二种方案是将上一种（前一章节的图三）横向旋转90度，假如横向旋转180度商品的占用空间和不旋转没有任何区别，所以该情况也不考虑，通过总结得出四种方案，以下将列出其余的三种方案。</p>

<p class="introduction">第二种方案</p>

<p class="introduction">　　将箱子内部的商品，在商品正常摆放（正面朝上）情况下，横向向右旋转90度得到下图的三个新的箱子（剩余空间）：</p>

<p class="introduction"><img src="https://img-blog.csdn.net/20160706114313170" alt="这里写图片描述" title=""></p>

<p class="introduction">第三种方案</p>

<p class="introduction">　　如果商品不介意放倒（平躺）的话，那么就会有第三种方案，在商品正常摆放（正面朝上）情况下，向左倾斜90度，得到如下三个剩余空间：</p>

<p class="introduction"><img src="https://img-blog.csdn.net/20160706114406327" alt="这里写图片描述" title=""></p>

<p class="introduction">第四种方案</p>

<p class="introduction">　　如果商品不介意放倒（平躺）的话，那么就会有第四种方案，在商品处于第三种方案摆放姿态的情况下，向右旋转90度，得到如下三个剩余空间：</p>

<p class="introduction"><img src="https://img-blog.csdn.net/20160706114602603" alt="这里写图片描述" title=""></p>

<p class="introduction">(八) 小结 </p>

<p class="introduction">　　上述内容，对一次装箱过程进行了分解，每次需要一个商品和一个空箱子，同时会产生三块新的更小的剩余空间。这三块剩余空间又可以看作是新的箱子。和新的合适自己的空间大小的商品去匹配。</p>

<p class="introduction">　　那么，这就是一个递归算法，方法输入一款商品和一个箱子，每一次递归都会产生三个新的箱子，新的箱子又可以装入其它更小的商品。</p>

<p class="introduction">　　如此循环往复，直到每一个新的箱子连最小的商品都装不下了，或者没有任何商品可以装进箱子里了，这个过程就会自动结束。这是递归的出口条件。</p>

<p class="introduction">　　其实，当然没有那么简单，图二所示的商品放入箱子的方法至少分为4种，每一种都需要尝试一下，以求得最合理的剩余空间；另外还有一订单多商品多箱子。</p>

<p class="introduction">(九) 可能存在的问题</p>

<p class="introduction">1，  箱子没有实际装满（”装满”的定义非本文中定义的）。 <br>
2，  用户会认为系统推荐的装箱方案不是太合理的安排。特型箱被匹配到常规商品上。例如：可能会有一种特别细长的箱型、而某种商品又恰好长宽都匹配，一字码放很多件后，刚好填满，这也是一个很奇葩的结局，需要特别考虑。 <br>
3，  部分箱型和商品需要手动匹配，因为某些箱子本来就是专门为指定的商品量身定做的。</p>

</div>

<a href="https://search.maven.org/remote_content?g=com.alibaba&a=fastjson&v=LATEST">FastJSON.jar 下载</a>

<link href="https://csdnimg.cn/release/phoenix/mdeditor/markdown_views-7b4cdcb592.css" rel="stylesheet">
</div>
