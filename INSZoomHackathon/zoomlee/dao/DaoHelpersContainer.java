package com.zoomlee.Zoomlee.dao;

import com.zoomlee.Zoomlee.net.model.BaseItem;
import com.zoomlee.Zoomlee.net.model.CategoriesDocumentsType;
import com.zoomlee.Zoomlee.net.model.Category;
import com.zoomlee.Zoomlee.net.model.Color;
import com.zoomlee.Zoomlee.net.model.Country;
import com.zoomlee.Zoomlee.net.model.Document;
import com.zoomlee.Zoomlee.net.model.DocumentsType;
import com.zoomlee.Zoomlee.net.model.DocumentsType2Field;
import com.zoomlee.Zoomlee.net.model.Field;
import com.zoomlee.Zoomlee.net.model.FieldsType;
import com.zoomlee.Zoomlee.net.model.File;
import com.zoomlee.Zoomlee.net.model.FilesType;
import com.zoomlee.Zoomlee.net.model.Form;
import com.zoomlee.Zoomlee.net.model.FormField;
import com.zoomlee.Zoomlee.net.model.Group;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.net.model.Tag;
import com.zoomlee.Zoomlee.net.model.Tax;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by  Evgen Marinin <ievgen.marinin@alterplay.com> on 22.01.15.
 */
public class DaoHelpersContainer<Entity extends BaseItem> {

    private static final DaoHelpersContainer INSTANCE = new DaoHelpersContainer();

    public static DaoHelpersContainer getInstance() {
        return INSTANCE;
    }

    private Map<Class, DaoHelper> daoHelpers = new HashMap<Class, DaoHelper>(14);

    private DaoHelpersContainer() {
        daoHelpers.put(Category.class, new CategoryDaoHelper());
        daoHelpers.put(CategoriesDocumentsType.class, new CategoriesDocsTypeDaoHelper());
        daoHelpers.put(Color.class, new ColorsDaoHelper());
        daoHelpers.put(Country.class, new CountryDaoHelper());
        daoHelpers.put(Document.class, new DocumentsDaoHelper());
        daoHelpers.put(DocumentsType.class, new DocumentsTypeDaoHelper());
        daoHelpers.put(DocumentsType2Field.class, new DocumentsType2FieldsDaoHelper());
        daoHelpers.put(Field.class, new FieldsDaoHelper());
        daoHelpers.put(FieldsType.class, new FieldsTypeDaoHelper());
        daoHelpers.put(File.class, new FilesDaoHelper());
        daoHelpers.put(FilesType.class, new FilesTypeDaoHelper());
        daoHelpers.put(Group.class, new GroupsDaoHelper());
        daoHelpers.put(Person.class, new PersonsDaoHelper());
        daoHelpers.put(Tag.class, new TagsDaoHelper());
        daoHelpers.put(Tax.class, new TaxDaoHelper());
        daoHelpers.put(Form.class, new FormsDaoHelper());
        daoHelpers.put(FormField.class, new FormFieldsDaoHelper());
    }

    public DaoHelper<Entity> getDaoHelper(Class<Entity> itemClazz) {
        DaoHelper<Entity> daoHelper = (DaoHelper<Entity>) daoHelpers.get(itemClazz);
        return daoHelper;
    }
}
