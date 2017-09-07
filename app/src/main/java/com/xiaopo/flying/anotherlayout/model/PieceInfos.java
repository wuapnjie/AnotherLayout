package com.xiaopo.flying.anotherlayout.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author wupanjie
 */
public class PieceInfos {
  @SerializedName("pieces")
  public List<PieceInfo> pieces;
  @SerializedName("image_path")
  public String imagePath;
}
