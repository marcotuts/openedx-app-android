package org.openedx.profile.presentation.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import org.openedx.core.R
import org.openedx.core.UIMessage
import org.openedx.profile.domain.model.Account
import org.openedx.core.module.DownloadWorkerController
import org.openedx.core.system.AppCookieManager
import org.openedx.core.system.ResourceManager
import org.openedx.profile.domain.interactor.ProfileInteractor
import org.openedx.profile.presentation.ProfileAnalytics
import org.openedx.profile.system.notifier.AccountUpdated
import org.openedx.profile.system.notifier.ProfileNotifier
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.openedx.core.data.storage.CorePreferences
import org.openedx.profile.data.storage.ProfilePreferences
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()
    private val dispatcherIO = UnconfinedTestDispatcher()

    private val resourceManager = mockk<ResourceManager>()
    private val preferencesManager = mockk<ProfilePreferences>()
    private val interactor = mockk<ProfileInteractor>()
    private val notifier = mockk<ProfileNotifier>()
    private val cookieManager = mockk<AppCookieManager>()
    private val workerController = mockk<DownloadWorkerController>()
    private val analytics = mockk<ProfileAnalytics>()

    private val account = mockk<Account>()

    private val noInternet = "Slow or no internet connection"
    private val somethingWrong = "Something went wrong"

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        every { resourceManager.getString(R.string.core_error_no_connection) } returns noInternet
        every { resourceManager.getString(R.string.core_error_unknown_error) } returns somethingWrong
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getAccount no internetConnection and cache is null`() = runTest {
        val viewModel =
            ProfileViewModel(
                interactor,
                preferencesManager,
                resourceManager,
                notifier,
                dispatcher,
                cookieManager,
                workerController,
                analytics
            )
        coEvery { preferencesManager.profile } returns null
        coEvery { interactor.getAccount() } throws UnknownHostException()
        advanceUntilIdle()

        coVerify(exactly = 1) { interactor.getAccount() }

        val message = viewModel.uiMessage.value as? UIMessage.SnackBarMessage
        assert(viewModel.uiState.value is ProfileUIState.Loading)
        assertEquals(noInternet, message?.message)
    }

    @Test
    fun `getAccount no internetConnection and cache is not null`() = runTest {
        val viewModel =
            ProfileViewModel(
                interactor,
                preferencesManager,
                resourceManager,
                notifier,
                dispatcher,
                cookieManager,
                workerController,
                analytics
            )
        coEvery { preferencesManager.profile } returns account
        coEvery { interactor.getAccount() } throws UnknownHostException()
        advanceUntilIdle()

        coVerify(exactly = 1) { interactor.getAccount() }

        val message = viewModel.uiMessage.value as? UIMessage.SnackBarMessage
        assert(viewModel.uiState.value is ProfileUIState.Data)
        assertEquals(noInternet, message?.message)
    }

    @Test
    fun `getAccount unknown exception`() = runTest {
        val viewModel =
            ProfileViewModel(
                interactor,
                preferencesManager,
                resourceManager,
                notifier,
                dispatcher,
                cookieManager,
                workerController,
                analytics
            )
        coEvery { preferencesManager.profile } returns null
        coEvery { interactor.getAccount() } throws Exception()
        advanceUntilIdle()

        coVerify(exactly = 1) { interactor.getAccount() }

        val message = viewModel.uiMessage.value as? UIMessage.SnackBarMessage
        assert(viewModel.uiState.value is ProfileUIState.Loading)
        assertEquals(somethingWrong, message?.message)
    }

    @Test
    fun `getAccount success`() = runTest {
        val viewModel =
            ProfileViewModel(
                interactor,
                preferencesManager,
                resourceManager,
                notifier,
                dispatcher,
                cookieManager,
                workerController,
                analytics
            )
        coEvery { preferencesManager.profile } returns null
        coEvery { interactor.getAccount() } returns account
        every { preferencesManager.profile = any() } returns Unit
        advanceUntilIdle()

        coVerify(exactly = 1) { interactor.getAccount() }

        assert(viewModel.uiState.value is ProfileUIState.Data)
        assert(viewModel.uiMessage.value == null)
    }

    @Test
    fun `logout no internet connection`() = runTest {
        val viewModel =
            ProfileViewModel(
                interactor,
                preferencesManager,
                resourceManager,
                notifier,
                dispatcher,
                cookieManager,
                workerController,
                analytics
            )
        coEvery { interactor.logout() } throws UnknownHostException()
        coEvery { workerController.cancelWork() } returns Unit

        viewModel.logout()
        advanceUntilIdle()

        coVerify(exactly = 1) { interactor.logout() }

        val message = viewModel.uiMessage.value as? UIMessage.SnackBarMessage
        assertEquals(noInternet, message?.message)
        assert(viewModel.successLogout.value == null)
    }

    @Test
    fun `logout unknown exception`() = runTest {
        val viewModel =
            ProfileViewModel(
                interactor,
                preferencesManager,
                resourceManager,
                notifier,
                dispatcher,
                cookieManager,
                workerController,
                analytics
            )
        coEvery { interactor.logout() } throws Exception()
        coEvery { workerController.cancelWork() } returns Unit
        viewModel.logout()
        advanceUntilIdle()

        coVerify(exactly = 1) { interactor.logout() }

        val message = viewModel.uiMessage.value as? UIMessage.SnackBarMessage
        assertEquals(somethingWrong, message?.message)
        assert(viewModel.successLogout.value == null)
    }

    @Test
    fun `logout success`() = runTest {
        val viewModel = ProfileViewModel(
            interactor,
            preferencesManager,
            resourceManager,
            notifier,
            dispatcher,
            cookieManager,
            workerController,
            analytics
        )
        coEvery { preferencesManager.profile } returns mockk()
        coEvery { interactor.getAccount() } returns mockk()
        every { analytics.logoutEvent(false) } returns Unit
        every { preferencesManager.profile = any() } returns Unit
        coEvery { interactor.logout() } returns Unit
        coEvery { workerController.cancelWork() } returns Unit
        every { cookieManager.clearWebViewCookie() } returns Unit
        viewModel.logout()
        advanceUntilIdle()
        coVerify(exactly = 1) { interactor.logout() }
        verify { analytics.logoutEvent(false) }

        assert(viewModel.uiMessage.value == null)
        assert(viewModel.successLogout.value == true)
    }

    @Test
    fun `AccountUpdated notifier test`() = runTest {
        val viewModel = ProfileViewModel(
            interactor,
            preferencesManager,
            resourceManager,
            notifier,
            dispatcher,
            cookieManager,
            workerController,
            analytics
        )
        coEvery { preferencesManager.profile } returns null
        every { notifier.notifier } returns flow { emit(AccountUpdated()) }
        val mockLifeCycleOwner: LifecycleOwner = mockk()
        val lifecycleRegistry = LifecycleRegistry(mockLifeCycleOwner)
        lifecycleRegistry.addObserver(viewModel)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

        advanceUntilIdle()

        coVerify(exactly = 2) { interactor.getAccount() }
    }

}