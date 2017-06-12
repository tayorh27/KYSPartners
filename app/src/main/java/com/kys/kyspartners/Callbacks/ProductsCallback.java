package com.kys.kyspartners.Callbacks;

import com.kys.kyspartners.Information.Products;

import java.util.ArrayList;

/**
 * Created by sanniAdewale on 04/02/2017.
 */

public interface ProductsCallback {

    void onProductsLoaded(ArrayList<Products> products);
}
