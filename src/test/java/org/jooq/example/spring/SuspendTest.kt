package org.jooq.example.spring

import kotlinx.coroutines.runBlocking
import org.jooq.DSLContext
import org.jooq.example.spring.service.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.EnableTransactionManagement


//@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Application::class])
@EnableTransactionManagement
class SuspendTest {
    @Autowired
    lateinit var dls:DSLContext

    @Autowired
    lateinit var userService: UserService

    @Test
     fun getUserInfoSuspend(){
        runBlocking{
            userService.registerSuspend()
        }
    }
    @Test
    fun listSuspend(){
        runBlocking{
            userService.listSuspend()
        }
    }

    @Test
    fun batchUserInfo() {
        val result = userService.batchUser(listOf(7,10,100,101,102,110))
        print(result)
    }
    @Test
    fun listUserInfo() {
        userService.listUser()
    }
}