package org.rapidoid.net;

import org.rapidoid.config.Conf;
import org.rapidoid.net.tls.TLSUtil;
import org.rapidoid.util.MscOpts;

import javax.net.ssl.SSLContext;

public class TLSParams {

	// auto-activate if TLS is enabled
	private volatile boolean tls = MscOpts.isTLSEnabled();

	private volatile String keystore = Conf.TLS.entry("keystore").or("");

	private volatile char[] keystorePassword = Conf.TLS.entry("keystorePassword").or("").toCharArray();

	private volatile char[] keyManagerPassword = Conf.TLS.entry("keyManagerPassword").or("").toCharArray();

	private volatile String truststore = Conf.TLS.entry("truststore").or("");

	private volatile char[] truststorePassword = Conf.TLS.entry("truststorePassword").or("").toCharArray();

	private volatile boolean selfSignedTLS = Conf.TLS.is("selfSigned");

	private volatile SSLContext tlsContext;

	public boolean tls() {
		return tls;
	}

	public TLSParams tls(boolean tls) {
		this.tls = tls;
		return this;
	}

	public String keystore() {
		return keystore;
	}

	public TLSParams keystore(String keystore) {
		this.keystore = keystore;
		return this;
	}

	public char[] keystorePassword() {
		return keystorePassword;
	}

	public TLSParams keystorePassword(char[] keystorePassword) {
		this.keystorePassword = keystorePassword;
		return this;
	}

	public char[] keyManagerPassword() {
		return keyManagerPassword;
	}

	public TLSParams keyManagerPassword(char[] keyManagerPassword) {
		this.keyManagerPassword = keyManagerPassword;
		return this;
	}

	public String truststore() {
		return truststore;
	}

	public TLSParams truststore(String truststore) {
		this.truststore = truststore;
		return this;
	}

	public char[] truststorePassword() {
		return truststorePassword;
	}

	public TLSParams truststorePassword(char[] truststorePassword) {
		this.truststorePassword = truststorePassword;
		return this;
	}

	public boolean selfSignedTLS() {
		return selfSignedTLS;
	}

	public TLSParams selfSignedTLS(boolean selfSignedTLS) {
		this.selfSignedTLS = selfSignedTLS;
		return this;
	}

	public SSLContext tlsContext() {
		return tlsContext;
	}

	public TLSParams tlsContext(SSLContext tlsContext) {
		this.tlsContext = tlsContext;
		return this;
	}

	public synchronized SSLContext buildTLSContext() {
		if (tls && tlsContext == null) {
			tlsContext = TLSUtil.createContext(keystore, keystorePassword, keyManagerPassword, truststore, truststorePassword, selfSignedTLS);
		}

		// don't provide TLS context unless TLS is enabled
		return tls ? tlsContext : null;
	}

}
