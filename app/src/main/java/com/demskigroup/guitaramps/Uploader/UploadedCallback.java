package com.demskigroup.guitaramps.Uploader;
import java.util.List;
/**
 *@since  8/19/2017.
 */
public interface UploadedCallback
{
    void onSuccess(List<ProductImageDatas> data_list, List<ProductImageDatas> failed_list);

    void onError(String error);
}
