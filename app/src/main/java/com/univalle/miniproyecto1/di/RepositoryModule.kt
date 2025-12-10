import com.univalle.miniproyecto1.repository.InventoryRepository

import com.univalle.miniproyecto1.repository.InventoryRepositoryImpl

import dagger.Binds

import dagger.Module

import dagger.hilt.InstallIn

import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton



@Module

@InstallIn(SingletonComponent::class)

abstract class RepositoryModule {



    @Binds

    @Singleton

    abstract fun bindInventoryRepository(

        inventoryRepositoryImpl: InventoryRepositoryImpl

    ): InventoryRepository

}