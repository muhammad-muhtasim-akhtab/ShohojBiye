package com.example.shohojbiye.ui.auth

import com.example.shohojbiye.data.repository.UserRepository
import com.example.shohojbiye.util.SessionManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val userRepository = mockk<UserRepository>()
    private val sessionManager = mockk<SessionManager>()
    private lateinit var viewModel: AuthViewModel
    
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(userRepository, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_success_updatesStateToAuthenticated() = runTest {
        coEvery { userRepository.loginWithEmail(any(), any()) } returns Result.success(Unit)
        every { sessionManager.getUserRole() } returns "KAZI"

        viewModel.login("test@example.com", "password")

        assertEquals(AuthViewModel.AuthState.Authenticated("KAZI"), viewModel.authState.value)
    }

    @Test
    fun login_failure_updatesStateToError() = runTest {
        coEvery { userRepository.loginWithEmail(any(), any()) } returns Result.failure(Exception("Invalid credentials"))

        viewModel.login("test@example.com", "wrong")

        val state = viewModel.authState.value
        assertEquals(true, state is AuthViewModel.AuthState.Error)
    }
}
