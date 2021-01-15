package com.file.fileupload;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FileuploadApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public  void test(String args[]) {
        String Str = new String("Runoob");

        System.out.print("返回值 :" );
        System.out.println(Str.replace('o', 'T'));

        System.out.print("返回值 :" );
        System.out.println(Str.replace('u', 'D'));
    }

}
