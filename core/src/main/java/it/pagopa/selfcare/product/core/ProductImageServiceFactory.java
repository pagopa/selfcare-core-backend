package it.pagopa.selfcare.product.core;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductImageServiceFactory {

    private static final String SAVE_LOGO = "logo";
    private static final String SAVE_DEPICT_IMG = "depict";

    private final BeanFactory beanFactory;


    @Autowired
    public ProductImageServiceFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    public ProductImageService getInstance(String operation) {

        switch (operation) {
            case SAVE_LOGO:
                return beanFactory.getBean(ProductLogoImageServiceImpl.class);
            case SAVE_DEPICT_IMG:
                return beanFactory.getBean(ProductDepictImageServiceImpl.class);
            default:
                throw new IllegalArgumentException();
        }
    }

}
