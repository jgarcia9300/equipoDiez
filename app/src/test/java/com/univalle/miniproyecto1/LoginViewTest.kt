package com.univalle.miniproyecto1 // Asegúrate de usar TU paquete correcto

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.univalle.miniproyecto1.repository.InventoryRepository
import com.univalle.miniproyecto1.viewmodel.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    // live data sincrono
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    // objetos falsos (mocks)
    @Mock
    private lateinit var repository: InventoryRepository

    // observer para ver qué valores emiten los LiveData
    @Mock
    private lateinit var loginObserver: Observer<Boolean>
    @Mock
    private lateinit var errorObserver: Observer<String>
    @Mock
    private lateinit var registerObserver: Observer<Boolean>

    // configurar corrutinas test
    private val testDispatcher = StandardTestDispatcher()

    // objeto a probar
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // reemplazar hilo principal por dispatch
        Dispatchers.setMain(testDispatcher)

        // inyeccion repositorio falso
        viewModel = LoginViewModel(repository)

        // conectar los observer a los livedata
        viewModel.loginState.observeForever(loginObserver)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.registerState.observeForever(registerObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // limpieza
    }

    //  test login coloca loginstate true si el repo devuelve exito
    @Test
    fun loginStateTrue() = runTest {
        // given
        val email = "test@email.com"
        val pass = "123456"
        // si llaman a login devuelve true
        whenever(repository.login(email, pass)).thenReturn(true)

        // when
        viewModel.login(email, pass)

        // avanza el scheduler hasta que no hayan tareas
        advanceUntilIdle()

        // then
        verify(loginObserver).onChanged(true) //verificar si el livedata cambio a true
        verify(errorObserver, never()).onChanged(any()) // verificar que no hubo error
    }

    // test login test login coloca loginstate false si el repo devuelve fallo ---
    @Test
    fun loginStateFalse() = runTest {
        // given
        val email = "test@email.com"
        val pass = "passwordfail"
        whenever(repository.login(email, pass)).thenReturn(false)

        // when
        viewModel.login(email, pass)
        advanceUntilIdle()

        // then
        verify(loginObserver).onChanged(false)
        verify(errorObserver).onChanged("Login incorrecto o error de conexión")
    }

    // -test validacion campos vacios. login no llama repositorio si los campos son vacios ---
    @Test
    fun loginCamposVacios() = runTest {
        // given
        val email = ""
        val pass = ""

        // when
        viewModel.login(email, pass)
        // validacion sincrona

        // then
        verify(errorObserver).onChanged("Los campos no pueden estar vacíos")
        // verificacion que no se llame el repositorio
        verify(repository, never()).login(any(), any())
    }

    // test registro exitoso. registerState true si el repositorio tiene exito ---
    @Test
    fun registroExitoso() = runTest {
        // given
        val email = "nuevo@email.com"
        val pass = "321"
        whenever(repository.register(email, pass)).thenReturn(true)

        // when
        viewModel.register(email, pass)
        advanceUntilIdle()

        // then
        verify(registerObserver).onChanged(true)
    }

    // test verificar sesion activa. loginState true si el repositorio tiene exito ---
    @Test
    fun `checkSession debe poner loginState en TRUE si el repo dice que hay usuario`() {
        // given
        whenever(repository.isUserLoggedIn()).thenReturn(true)

        // when
        viewModel.checkSession()

        // then
        verify(loginObserver).onChanged(true)
    }
}