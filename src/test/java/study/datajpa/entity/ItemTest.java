package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.repository.ItemRepository;

@SpringBootTest
class ItemTest {

    @Autowired
    ItemRepository itemRepository;
    
    @Test
    public void save() throws Exception {
        //given
        Item item = new Item("A");
        /**
         * save는 내부적으로 entity가 신규면 persist 아니면 merge로 동작함
         * 문제는 id에 @GeneratedValue를 쓰지않고 임의로 값을 넣는경우 id가 null여부로 isNew를 판단하기 때문에 항상 merge로 동작하게됨
         * merge는 db에 id값으로 셀렉트를 날린뒤 결과값을 모두 바꾸는 식으로 동작하기 때문에 비효율이 발생함
         * 따라서 id값을 직접 생성하는경우 Persistable 인터페이스를 상속받아 직점 신규엔티티여부를 판단하는 메서드를 작성해야함
         */
        itemRepository.save(item);

        //when
        //then
    }

}