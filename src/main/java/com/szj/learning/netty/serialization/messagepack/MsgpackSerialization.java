package com.szj.learning.netty.serialization.messagepack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.szj.learning.netty.serialization.User;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.Templates;
import org.msgpack.unpacker.Unpacker;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/6 8:17 下午
 * @Description
 */
public class MsgpackSerialization {

    public static void main(String[] args) throws IOException {
//        List<String> list = new ArrayList();
//        list.add("a");
//        list.add("b");
//        list.add("c");
//        MessagePack msgpack = new MessagePack();
//        // obj -> bytes
//        byte[] bytes = msgpack.write(list);
//
//        // bytes -> obj
//        List<String> read = msgpack.read(bytes, Templates.tList(Templates.TString));
//        System.out.println(read);

        User szj = new User("szj", 1001);

        MessagePack messagePack = new MessagePack();
        messagePack.register(User.class, new UserTemplate());
        byte[] bytes = messagePack.write(szj);

        User read = messagePack.read(bytes, User.class);
        System.out.println(read);
    }

    private static class UserTemplate extends AbstractTemplate<User> {

        @Override
        public void write(Packer packer, User user, boolean required) throws IOException {
            if (user == null) {
                if (required) {
                    throw new RuntimeException("user 对象不能为空");
                }
                packer.writeNil();
                return;
            }
            // 告诉 packer，byte 数组里装属性
            packer.writeArrayBegin(2);
            packer.write(user.getName());
            packer.write(user.getUid());
            packer.writeArrayEnd();
        }

        @Override
        public User read(Unpacker unpacker, User user, boolean required) throws IOException {
            // trySkipNil 检查当前从 Unpacker 读取的值是否为 nil。如果是，则跳过它，并返回true；否则不执行任何操作并返回false。
            if (!required && unpacker.trySkipNil()) {
                return null;
            }
            unpacker.readArrayBegin();
            String name = unpacker.readString();
            int uid = unpacker.readInt();
            unpacker.readArrayEnd();
            // 如果入参中的 user 有值，这里就不会再新建对象，而是使用入参的 user
            return new User(name, uid);
        }
    }

}
