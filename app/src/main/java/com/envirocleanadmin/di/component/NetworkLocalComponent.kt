package com.envirocleanadmin.di.component

import com.envirocleanadmin.ui.activity.*

import dagger.Component

//Created by imobdev-rujul on 2/1/19
@Component(dependencies = [LocalDataComponent::class, NetworkComponent::class])
interface NetworkLocalComponent {
    fun injectSplashActivity(splashActivity: SplashActivity)
    fun injectLoginActivity(loginActivity: LoginActivity)
    fun injectForgetPasswordActivity(forgetPasswordActivity: ForgetPasswordActivity)
    fun injectCommunityActivity(communityActivity: CommunityActivity)
    fun injectCommunityDetailsActivity(communityDetailsActivity: CommunityDetailsActivity)

}