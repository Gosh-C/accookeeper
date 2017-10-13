package gosh.com.accookeepersdk.model;

/**
 * Created by goshchan on 13/10/2017.
 */

public class SheetField {
    private String mFieldName;
    private String mType;
    private Boolean mIsMandantory;

    public SheetField(String name , String type, Boolean isMandate){
        mFieldName = name;
        mType = type;
        mIsMandantory = isMandate;
    }

    public String getFieldName() {
        return mFieldName;
    }

    public void setFieldName(String mFieldName) {
        this.mFieldName = mFieldName;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public Boolean IsMandantory() {
        return mIsMandantory;
    }

    public void setIsMandantory(Boolean mIsMandantory) {
        this.mIsMandantory = mIsMandantory;
    }
}
