package packaging;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * @version V1.0
 * @Package: cn.edu.ruc.iir.xpace.algorithm
 * @ClassName: GoodsInBox
 * @Description: 商品装箱服务
 * @author: taoyouxian
 * @date: Create in 2018-09-25 13:34
 **/
public class GoodsInBox {

    private int runcount = 0;
    /* 箱子的型号,盛放空间 */
    private List<Map<String, Object>> boxTypeList;
    /* 订单中的商品 */
    private List<Map<String, Object>> goodsList;
    /* 计算结果 */
    private Set<Map<String/* 箱子的型号描述 */, Object>> result = new HashSet<Map<String, Object>>();
    /* 箱子装东西的情况，key 规则：箱型_箱子id */
    private Map<Object, Object> boxes = new HashMap<Object, Object>();

    /**
     * 根据箱型以及订单中的商品，返回每个箱型需要箱子多少只。如果没有任何的箱子能装下某一款超大商品的时候，抛出异常
     *
     * @param boxTypeList
     * @param goodsList
     * @return
     */
    public GoodsInBox(List<Map<String, Object>> boxTypeList, List<Map<String, Object>> goodsList) {
        this.boxTypeList = boxTypeList;
        this.goodsList = goodsList;
        // 开始执行
        run();
    }

    // 执行装箱
    private void run() {
        // 预先跑一遍看看，有没有特别大的东西
        double gl = 0d, gw = 0d, gh = 0d;
        double bl = 0d, bw = 0d, bh = 0d;
        int num = 0;
        for (Map<String, Object> g : goodsList) {
            num += Integer.valueOf(g.get("n").toString());
            gl = Double.max(gl, Double.valueOf(g.get("l").toString()));
            gw = Double.max(gw, Double.valueOf(g.get("w").toString()));
            gh = Double.max(gh, Double.valueOf(g.get("h").toString()));
        }
        for (Map<String, Object> b : boxTypeList) {
            bl = Double.max(bl, Double.valueOf(b.get("l").toString()));
            bw = Double.max(bw, Double.valueOf(b.get("w").toString()));
            bh = Double.max(bh, Double.valueOf(b.get("h").toString()));
        }
        if (gl > bl && gl > bw) {
            throw new java.lang.RuntimeException("估算失败，存在体积过大商品，请重新选择后再试。（长度超出范围）");
        }
        if (gw > bw && gw > bl) {
            throw new java.lang.RuntimeException("估算失败，存在体积过大商品，请重新选择后再试。（宽度超出范围）");
        }
        if (gh > bh) {
            throw new java.lang.RuntimeException("估算失败，存在体积过大商品，请重新选择后再试。（高度超出范围）");
        }
        if (num > 500) {
            throw new java.lang.RuntimeException("估算失败，商品数量过多，请重新选择后再试。（大于500件）");
        }

        for (final Map<String, Object> abox : boxTypeList) {
            tryInSpance(abox/* 给一个盒子 */, new java.util.ArrayList<Map<String, Object>>() {
                {
                    this.add(abox);
                }
            }/* 什么都没有装，盒子的空间还是这个盒子 */, false/* 已知目前这个盒子没有满 */, (goodsList)/* 急需进入盒子的商品 */, boxTypeList);
        }
    }

    /**
     * 每次测试1块空间，和全部商品，将商品依次向空间转移，放进去后产生新的3块空间， 同时商品的数量再减少，直到商品全部转移；
     *
     * @param box        一只箱子
     * @param moreSpance 这只箱子还剩余多少空间，第一次装进一件物品后还剩三块多余的，第二次再装进一件物品后，之前的空间少一块，同时又多出更小的三块...
     * @param boxIsFull  箱子满了吗？不能再装下任何东西了吗？
     * @param moreGoods  还剩多少件商品没有被装到箱子里去。
     * @param boxTypes
     * @return 尝试装一件商品后返回剩下的三块空间，返回null，表示不能再装了（满）。
     */
    /* 尝试装一件商品后返回剩下的三块空间 */
    private List<Map<String, Object>> tryInSpance(Map<String/* 长l宽w高h等 */, Object> box/* 某1个盒子 */, List<Map<String, Object>> moreSpance/* 某一个盒子的剩余空间 */,
                                                  boolean boxIsFull, List<Map<String, Object>> moreGoods, List<Map<String, Object>> boxTypes /* 可用的箱子型号 */) {
        this.runcount++;
        // 为新的箱子分配一个箱子的唯一id，箱子型號+uuid保證每一個箱子的id唯一。箱子被裝滿之後，箱子的屬性為滿。
        // 如果没有boxid表示是一个新的箱子
        // 如果之前的箱子满了也需要新的箱子
        if (null == box.get("boxid") || boxIsFull) {
            box.put("boxid", box.get("id").toString().concat("_").concat(java.util.UUID.randomUUID().toString()));
            moreSpance = new java.util.ArrayList<Map<String, Object>>();
            moreSpance.add(box);
        }

        System.out.print("第");
        System.out.print(runcount);
        System.out.print("次：");
        System.out.print(boxIsFull ? "满" : "未满");
        System.out.println();
        System.out.print("盒子：");
        System.out.print(JSON.toJSONString(box));
        System.out.println();
        System.out.print("可用的空间：");
        System.out.print(JSON.toJSONString(moreSpance));
        System.out.println();
        System.out.print("还有多少商品：");
        System.out.print(JSON.toJSONString(moreGoods));
        System.out.println();
        System.out.println();

        if (null == box || null == moreGoods || null == moreSpance)
            return null;
        // 是否有东西被装进去？
        boolean in = false;
        // 遍历这个箱子的剩余空间；
        loops:
        for (int bi = moreSpance.size() - 1; bi >= 0; bi--) {
            Map<String, Object> b = moreSpance.get(bi);
            if (null == b) {
                continue loops;
            }
            double bl = Double.valueOf(b.get("l").toString());
            double bw = Double.valueOf(b.get("w").toString());
            double bh = Double.valueOf(b.get("h").toString());

            if (bl <= 0 || bw <= 0 || bh <= 0)
                continue;

            // 遍历未装箱的商品列表
            loopg:
            for (int gi = moreGoods.size() - 1; gi >= 0; gi--) {
                Map<String, Object> g = moreGoods.get(gi);
                if (null == g.get("n") || null == g.get("sku"))
                    continue;
                Integer sku = Integer.valueOf(g.get("sku").toString());
                // 商品数量
                Integer num = Integer.valueOf(g.get("n").toString());
                if (0 == num) {
                    moreGoods.remove(gi);
                    // 继续下一个商品
                    continue loopg;
                }
                boolean goodsinbox = false;
                // 多少件商品就循环多少次，每次处理一件；
                loopn:
                for (int i = num; i >= 0;
                     i--) {
                    String code = box.get("code").toString().concat(",").concat(sku.toString());
                    double gl = Double.valueOf(g.get("l").toString());
                    double gw = Double.valueOf(g.get("w").toString());
                    double gh = Double.valueOf(g.get("h").toString());
                    Integer t = Integer.valueOf(g.get("t").toString());
                    // 0,可平躺可码放不可倒置；1，不可平躺可码放不可倒置；2,不可平躺不可码放不可倒置

                    // 正面放置商品
                    if ((bl - gl) >= 0d && (bw - gw) >= 0d && (bh - gh) >= 0d) {
                        // 这件商品被装进了这个盒子；是正着被放进去的。
                        g.put("boxid", box.get("boxid"));
                        // 商品的数量要减少一个
                        g.put("n", i - 1);
                        // 剩余空间需要减少一个
                        moreSpance.remove(bi);
                        in = true;
                        boxes.put(box.get("boxid"), g);

                        // 正放的3块剩余空间
                        if (2 != t.intValue() && bh - gh > 0d) {
                            Map<String, Object> leftover;
                            // 第一块空间 (盒子上面的剩余空间) bh - gh 高度相减表示盒子正上方
                            leftover = new HashMap<String, Object>();
                            leftover.put("boxid", box.get("boxid"));
                            leftover.put("id", box.get("id"));
                            leftover.put("l", gl);
                            leftover.put("w", gw);
                            leftover.put("h", bh - gh);
                            leftover.put("code", code);
                            moreSpance.add(leftover);
                        }
                        // 第二块空间
                        if (bw - gw > 0d) {
                            Map<String, Object> leftover;
                            leftover = new HashMap<String, Object>();
                            leftover.put("boxid", box.get("boxid"));
                            leftover.put("id", box.get("id"));

                            leftover.put("l", gl);
                            leftover.put("w", bw - gw);
                            leftover.put("h", bh);
                            leftover.put("code", code);
                            moreSpance.add(leftover);

                        }
                        // 第三块空间
                        if (bl - gl > 0d) {
                            Map<String, Object> leftover;
                            leftover = new HashMap<String, Object>();
                            leftover.put("boxid", box.get("boxid"));
                            leftover.put("id", box.get("id"));

                            leftover.put("l", bl - gl);
                            leftover.put("w", bw);
                            leftover.put("h", bh);
                            leftover.put("code", code);
                            moreSpance.add(leftover);
                        }

                        tryInSpance(box, moreSpance, moreSpance.isEmpty() ? true : false, moreGoods, boxTypes);
                        return moreSpance;

                        // 侧面放置商品
                    } else if ((bl - gw) >= 0d && (bw - gl) >= 0d && (bh - gh) >= 0d) {
                        // 可以放入的情况下先减少商品的数量；
                        // 这件商品被装进了这个盒子；
                        g.put("boxid", box.get("boxid"));
                        // 商品的数量要减少一个
                        g.put("n", i - 1);
                        // 剩余空间需要减少一个
                        moreSpance.remove(bi);
                        in = true;
                        boxes.put(box.get("boxid"), g);
                        // 侧放的3块剩余空间
                        if (2 != t.intValue() && bh - gh > 0d) {
                            Map<String, Object> leftover;
                            // 第一块空间
                            leftover = new HashMap<String, Object>();
                            leftover.put("boxid", box.get("boxid"));
                            leftover.put("id", box.get("id"));

                            leftover.put("l", gl);
                            leftover.put("w", gw);
                            leftover.put("h", bh - gh);
                            leftover.put("code", code);
                            moreSpance.add(leftover);
                        }
                        // 第二块空间
                        if (bw - gl > 0d) {
                            Map<String, Object> leftover;
                            leftover = new HashMap<String, Object>();
                            leftover.put("boxid", box.get("boxid"));
                            leftover.put("id", box.get("id"));

                            leftover.put("l", bw - gl);
                            leftover.put("w", gw);
                            leftover.put("h", bh);
                            leftover.put("code", code);
                            moreSpance.add(leftover);
                        }
                        // 第三块空间
                        if (bl - gw > 0d) {
                            Map<String, Object> leftover;
                            leftover = new HashMap<String, Object>();
                            leftover.put("boxid", box.get("boxid"));
                            leftover.put("id", box.get("id"));

                            leftover.put("l", bl - gw);
                            leftover.put("w", bw);
                            leftover.put("h", bh);
                            leftover.put("code", code);
                            moreSpance.add(leftover);
                        }

                        tryInSpance(box, moreSpance, moreSpance.isEmpty() ? true : false, moreGoods, boxTypes);
                        return moreSpance;

                        // 卧倒放置商品
                    } else if (t.intValue() == 0d && (bl - gh) >= 0d && (bw - gw) >= 0d && (bw - gl) >= 0d) {
                        // 这件商品被装进了这个盒子；
                        g.put("boxid", box.get("boxid"));
                        // 商品的数量要减少一个
                        g.put("n", i - 1);
                        // 剩余空间需要减少一个
                        moreSpance.remove(bi);
                        in = true;
                        boxes.put(box.get("boxid"), g);
                        // 侧放的3块剩余空间

                        if (2 != t.intValue() && bh - gh > 0d) {
                            // 第一块空间
                            Map<String, Object> leftover;
                            leftover = new HashMap<String, Object>();
                            leftover.put("boxid", box.get("boxid"));
                            leftover.put("id", box.get("id"));

                            leftover.put("l", gh);
                            leftover.put("w", gw);
                            leftover.put("h", bh - gh);
                            leftover.put("code", code);
                            moreSpance.add(leftover);
                        }
                        // 第二块空间
                        if (bw - gw > 0d) {
                            Map<String, Object> leftover;
                            leftover = new HashMap<String, Object>();
                            leftover.put("boxid", box.get("boxid"));
                            leftover.put("id", box.get("id"));

                            leftover.put("l", bw - gw);
                            leftover.put("w", gh);
                            leftover.put("h", bh);
                            leftover.put("code", code);
                            moreSpance.add(leftover);
                        }
                        // 第三块空间
                        if (bl - gh > 0d) {
                            Map<String, Object> leftover;
                            leftover = new HashMap<String, Object>();
                            leftover.put("boxid", box.get("boxid"));
                            leftover.put("id", box.get("id"));

                            leftover.put("l", bl - gh);
                            leftover.put("w", bw);
                            leftover.put("h", bh);
                            leftover.put("code", code);
                            moreSpance.add(leftover);
                        }

                        tryInSpance(box, moreSpance, moreSpance.isEmpty() ? true : false, moreGoods, boxTypes);
                        return moreSpance;
                        // 侧卧放置商品
                    } else if (t.intValue() == 0d && (bl - gw) >= 0d && (bh - gl) >= 0d && (bw - gh) >= 0d) {
                        // 这件商品被装进了这个盒子；
                        g.put("boxid", box.get("boxid"));
                        // 商品的数量要减少一个
                        g.put("n", i - 1);
                        // 剩余空间需要减少一个
                        moreSpance.remove(bi);
                        in = true;
                        boxes.put(box.get("boxid"), g);
                        // 侧放的3块剩余空间

                        if (2 != t.intValue() && bh - gl > 0d) {
                            Map<String, Object> leftover;
                            // 第一块空间
                            leftover = new HashMap<String, Object>();
                            leftover.put("boxid", box.get("boxid"));
                            leftover.put("id", box.get("id"));

                            leftover.put("l", gw);
                            leftover.put("w", gh);
                            leftover.put("h", bh - gl);
                            leftover.put("code", code);
                            moreSpance.add(leftover);
                        }
                        // 第二块空间
                        if (bw - gh > 0d) {
                            Map<String, Object> leftover;
                            leftover = new HashMap<String, Object>();
                            leftover.put("boxid", box.get("boxid"));
                            leftover.put("id", box.get("id"));

                            leftover.put("l", bw - gh);
                            leftover.put("w", gw);
                            leftover.put("h", bh);
                            leftover.put("code", code);
                            moreSpance.add(leftover);
                        }
                        // 第三块空间
                        if (bl - gw > 0d) {
                            Map<String, Object> leftover;
                            leftover = new HashMap<String, Object>();
                            leftover.put("boxid", box.get("boxid"));
                            leftover.put("id", box.get("id"));

                            leftover.put("l", bl - gw);
                            leftover.put("w", bw);
                            leftover.put("h", bh);
                            leftover.put("code", code);
                            moreSpance.add(leftover);
                        }

                        tryInSpance(box, moreSpance, moreSpance.isEmpty() ? true : false, moreGoods, boxTypes);
                        return moreSpance;
                    }
                }
            }

        }
        // 任何东西都装不下了
        if (!in)
            box.put("boxid", box.get("id").toString().concat("_").concat(java.util.UUID.randomUUID().toString()));
        return null;
    }

    /**
     * 返回计算后得到的结果
     *
     * @return
     */
    public Set<Map<String, Object>> getResult() {
        result.clear();
        System.out.println(JSON.toJSONString(this.boxes));
        System.out.println("一共需要" + this.boxes.size() + "个箱子。");
        return this.result;
    }

    public static void main(String args[]) throws Exception {
        xiaoyunduo();
    }

    private static void xiaoyunduo() {
        List<Map<String, Object>> goodsList = new ArrayList<Map<String, Object>>();

        String csvPath = "H:\\SelfLearning\\SAI\\DBIIR_Github\\github\\Xspace\\xspace-algorithm\\src\\main\\resources\\data.csv";
        String line = null;
        String[] splits;
        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            while ((line = reader.readLine()) != null) {
                splits = line.split(",");
                Map m = new HashMap<String, Object>();
                m.put("title", splits[1] + "_" + splits[7]);
                m.put("sku", "222");
                m.put("l", splits[2]);
                m.put("w", splits[3]);
                m.put("h", splits[4]);
                m.put("n", 1);
                m.put("t", 0);
                goodsList.add(m);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("物品数量: " + goodsList.size());
        System.out.println("物品: " + JSON.toJSONString(goodsList));

        GoodsInBox gb;
        // 让我们测试一下
        gb = new GoodsInBox(new ArrayList<Map<String, Object>>() {
            {
                // 为了好计算，假设有一种车，只有1立方 1x1x1
                this.add(new HashMap<String, Object>() {
                    {
                        this.put("id", "1");
                        this.put("code", "1");
                        this.put("title", "1立方的车");
                        this.put("l", 2000.00d);
                        this.put("w", 1500.00d);
                        this.put("h", 2000.00d);
//                        this.put("bearing_capacity", 41000.00d);
                        this.put("icon", null);
                    }
                });
            }
        }, goodsList
//                new ArrayList<Map<String, Object>>() {
//            {
//                // 刚好商品也是1立方，1x1x1
//                this.add(new HashMap<String, Object>() {
//                    {
//                        this.put("sku", "222");
//                        this.put("title", "1立方商品");
//                        this.put("l", 100.00d);
//                        this.put("w", 100.00d);
//                        this.put("h", 100.00d);
//                        this.put("n", 1);
//                        this.put("t", 1);
//                    }
//                });
//            }
//        }
        );

        // 1车刚好装下
        System.out.println(JSON.toJSONString(gb.getResult()).toString());
        System.out.println("\n========================================\n");
    }

}
