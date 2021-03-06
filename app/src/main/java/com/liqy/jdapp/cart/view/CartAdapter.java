package com.liqy.jdapp.cart.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.liqy.jdapp.R;
import com.liqy.jdapp.cart.model.bean.Product;
import com.liqy.jdapp.cart.model.bean.Seller;

import java.util.ArrayList;
import java.util.List;

/**
 * @file FileName
 * 购物车
 * Copyright 星期一 YourCompany.
 */
public class CartAdapter extends BaseExpandableListAdapter {

    private List<Seller> sellers;//商家列表

    private LayoutInflater inflater;//初始化视图类
    private Context context;//上下文环境

    private TextView txt_total_price;//合计总价展示控件

    private ImageView img_check_all;//全选状态展示控件

    private boolean isCheckAll = false;//全选状态标记

    /**
     * 构造方法
     *
     * @param context
     */
    public CartAdapter(Context context) {
        this.sellers = new ArrayList<>();//TODO 在这里初始化，避免空指针错误，下面在使用就不需要判断空指针
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    /**
     * 绑定顶部的总计价格控件
     *
     * @param txt_total_price
     */
    public void setTotalPrice(TextView txt_total_price) {
        this.txt_total_price = txt_total_price;
    }

    /**
     * 绑定全选按钮的控件
     *
     * @param img_check_all
     */
    public void setCheckAll(ImageView img_check_all) {
        this.img_check_all = img_check_all;
    }

    public boolean isCheckAll() {
        return isCheckAll;
    }

    /**
     * 添加数据，这样写入数据，可以有效避免空指针操作
     *
     * @param list
     */
    public void addData(List<Seller> list) {
        sellers.addAll(list);//添加数据
        notifyDataSetChanged();//刷新数据
    }

    /**
     * 默认全部展开
     *
     * @param listView
     */
    public void expandGroup(ExpandableListView listView) {
        for (int i = 0; i < getGroupCount(); i++) {
            listView.expandGroup(i);
        }
    }

    /**
     * 计算所选产品价格,只要改变购物车，价格重新计算
     */
    private void sumPrice() {
        int total = 0;//计算总价，最后赋值给控件

        for (Seller seller : sellers) {//遍历商家
            if (seller.isCheck) {//选中的店铺
                for (Product product : seller.list) {
                    if (product.selected == 1) {//选中的产品的价格计算
                        //解析价格
                        int price = (int) Double.parseDouble(product.price);
                        //累加价格
                        total = total + (price * product.num);
                    }
                }
            }
        }

        //把最终结果赋值给控件
        txt_total_price.setText("合计￥" + total);
    }

    /**
     * 全选方案
     */
    public void checkAll() {

        isCheckAll = true;//全选

        for (Seller seller : sellers) {//遍历商家
            seller.isCheck = true;//设置选中
            for (Product product : seller.list) {//遍历产品
                product.selected = 1;//设置选中
            }
        }

        //更新UI
        updateSelectUI();

    }

    /**
     * 反选
     */
    public void invertAll() {

        isCheckAll = false;//反选

        for (Seller seller : sellers) {//遍历商家
            seller.isCheck = false;//设置未被选中
            for (Product product : seller.list) {//遍历产品
                product.selected = 0;//设置未被选中
            }
        }

        //更新UI
        updateSelectUI();
    }

    /**
     * 点击产品选中按钮
     *
     * @param product 产品
     * @param seller  商家
     */
    private void clickProductCheck(Seller seller, Product product) {
        //全未选中，选择中产品的时候商家也选中，全选按钮是未选中状态
        //全选中，只要有一个未选，影响自己，和全选按钮

        if (product.selected == 1) {
            product.selected = 0;//产品未选中
            //TODO 当多个产品需要处理
            int selectedNum = 0;
            for (Product ptemp : seller.list) {//遍历产品
                if (ptemp.selected == 1) {
                    selectedNum += 1;//选中增加1
                }
            }
            if (selectedNum > 0) {//产品只要一个选中，商家就是选中状态
                seller.isCheck = true;
            } else {
                seller.isCheck = false;
            }

        } else {
            product.selected = 1;//产品选中
            seller.isCheck = true;//有一个产品选中，商家即为选中状态
        }

        isCheckAll = isQueryCheckAll();//查询是否为全部选中状态

        updateSelectUI();//更新UI
    }

    /**
     * 检查是否有未选中的产品
     */
    public boolean isQueryCheckAll() {

        int total = 0;//全部商品
        int checkNum = 0;//选中产品

        for (Seller seller : sellers) {//遍历商家
            total += seller.list.size();//统计数量
            if (seller.isCheck) {//只需要遍历选中的商家
                for (Product product : seller.list) {//遍历产品
                    if (product.selected == 1) {//如果选中产品
                        checkNum += 1;//选中产品数量+1
                    }
                }
            }
        }

        return checkNum == total;//比较。相等，即全选
    }

    /**
     * 点击商家选中
     *
     * @param seller
     */
    private void clickSellerCheck(Seller seller) {

        //改变选中状态
        if (seller.isCheck) {
            seller.isCheck = false;
        } else {
            seller.isCheck = true;
        }

        //遍历产品
        for (Product product : seller.list) {
            if (seller.isCheck) {//选中商家 ，就是全选
                product.selected = 1;
            } else {
                product.selected = 0;
            }
        }

        isCheckAll = isQueryCheckAll();//查询是否为全部选中状态
        //更新UI
        updateSelectUI();

    }

    /**
     * 更新选中状态UI
     */
    public void updateSelectUI() {

        //更新界面
        if (isCheckAll) {
            img_check_all.setImageResource(R.drawable.ic_checked);
        } else {
            img_check_all.setImageResource(R.drawable.ic_uncheck);
        }

        sumPrice();

        //刷新界面
        notifyDataSetChanged();
    }

    /**
     * 获取商家数量
     *
     * @return
     */
    @Override
    public int getGroupCount() {
        return sellers.size();
    }

    /**
     * 商家下面有几个产品
     *
     * @param i
     * @return
     */
    @Override
    public int getChildrenCount(int i) {
        return sellers.get(i).list.size();
    }

    /**
     * 获取商家类
     *
     * @param i
     * @return
     */
    @Override
    public Seller getGroup(int i) {
        return sellers.get(i);
    }

    /**
     * 获取产品
     *
     * @param i
     * @param i1
     * @return
     */
    @Override
    public Product getChild(int i, int i1) {
        return sellers.get(i).list.get(i1);
    }

    /**
     * 固定ID
     *
     * @param i
     * @return
     */
    @Override
    public long getGroupId(int i) {
        Seller seller = getGroup(i);
        return seller.sellerid;
    }

    /**
     * 获取ID
     *
     * @param i
     * @param i1
     * @return
     */
    @Override
    public long getChildId(int i, int i1) {
        Product product = getChild(i, i1);
        return product.pid;
    }

    /**
     * 允许固定ID
     *
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * 初始化商家控件
     *
     * @param i
     * @param b
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        SellerViewHolder holder;

        //优化
        if (view == null) {
            view = inflater.inflate(R.layout.item_cart_seller, viewGroup, false);
            holder = new SellerViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (SellerViewHolder) view.getTag();
        }

        final Seller seller = getGroup(i);//获取商家

        holder.sellerName.setText(seller.sellerName);//商家名称

        //商家选择按钮
        if (seller.isCheck) {
            holder.sellerCheck.setImageResource(R.drawable.ic_checked);
        } else {
            holder.sellerCheck.setImageResource(R.drawable.ic_uncheck);
        }

        //设置选择按钮事件
        holder.sellerCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSellerCheck(seller);

            }
        });

        return view;
    }

    @Override
    public View getChildView(final int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        ProductViewHolder holder;

        //优化
        if (view == null) {
            view = inflater.inflate(R.layout.item_cart_product, viewGroup, false);
            holder = new ProductViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ProductViewHolder) view.getTag();
        }

        final Product product = getChild(i, i1);//获取产品

        holder.productName.setText(product.title);//产品名称
        holder.productPrice.setText("￥" + product.price);//TODO 产品价格，一般定义为字符串，防止数据格式解析错误

        //绑定产品数量
        holder.addSumView.setSum(product.num);

        //选中按钮状态UI更新
        if (product.selected == 1) {
            holder.productCheck.setImageResource(R.drawable.ic_checked);
        } else {
            holder.productCheck.setImageResource(R.drawable.ic_uncheck);
        }

        //设置产品选中监听
        holder.productCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //获取商家
                Seller seller = getGroup(i);

                clickProductCheck(seller, product);
            }
        });

        /**
         * 增加所购产品数量
         */
        holder.addSumView.setSumClickListener(new AddSumView.SumClickListener() {
            @Override
            public void sumClick(int psum) {
                product.num = psum;
                sumPrice();
            }
        });


        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }


    /**
     * 商家
     */
    static class SellerViewHolder {
        TextView sellerName;//商家名称
        ImageView sellerCheck;//选中按钮

        public SellerViewHolder(View view) {
            sellerName = (TextView) view.findViewById(R.id.txt_seller_name);
            sellerCheck = (ImageView) view.findViewById(R.id.img_select);
        }
    }

    /**
     * 产品
     */
    static class ProductViewHolder {
        TextView productName;//产品名称
        TextView productPrice;//产品价格
        ImageView productCheck;//选中按钮

        AddSumView addSumView;

        public ProductViewHolder(View view) {
            productName = (TextView) view.findViewById(R.id.txt_product_name);
            productPrice = (TextView) view.findViewById(R.id.txt_product_price);
            productCheck = (ImageView) view.findViewById(R.id.img_select);
            addSumView = (AddSumView) view.findViewById(R.id.add_sum);
        }

    }
}
