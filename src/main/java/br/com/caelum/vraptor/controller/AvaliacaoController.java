package br.com.caelum.vraptor.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.dao.AvaliacaoDAO;
import br.com.caelum.vraptor.dao.JPAUtil;
import br.com.caelum.vraptor.model.Avaliacao;
import br.com.caelum.vraptor.service.EntityManagerService;
import br.com.caelum.vraptor.view.Results;

@Controller
public class AvaliacaoController {

	@Inject Result result;

	@Get("/avaliacoes")
	public void buscaAvaliacoes() {
		
		EntityManager em = new JPAUtil().getEntityManager();
		try {
			List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
	
			//busca do Banco
			
			avaliacoes = new AvaliacaoDAO(em).lista();
			
			result.use(Results.json()).withoutRoot().from(avaliacoes).include("aluno").serialize();
		}finally {
			EntityManagerService.LiberaConnection(em);
		}
	}
	
	@Get("/avaliacoes/{avaliacao.id}")
	public void buscaAvaliacao(Avaliacao avaliacao) {
		
		EntityManager em = new JPAUtil().getEntityManager();
		try {
			//validacao
			if(avaliacao.getId() < 1) {
				result.use(Results.http()).sendError(400, "Você deve passar o ID");
				return;
			}
			
			
			AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO(em);
			
			Avaliacao avaliacaoDoBanco = (Avaliacao) avaliacaoDAO.SelectPorId(avaliacao);
			
			if(avaliacaoDoBanco == null) { //verifica se existe avaliacao com aquele ID
				result.use(Results.http()).sendError(400, "Com o ID informado não existe nenhum avaliacao no banco");
				return;
			}
				
			result.use(Results.json()).from(avaliacaoDoBanco).include("aluno").serialize();
		}finally {
			EntityManagerService.LiberaConnection(em);
		}
	}
	
	
	@Post("/avaliacoes")
	@Consumes("application/json")
	public void adicionaAvaliacao(Avaliacao avaliacao) {
		
		EntityManager em = new JPAUtil().getEntityManager();
		try {
			//validacoes
			if(avaliacao.getId() > 0) {
				result.use(Results.http()).sendError(400, "O avaliacao não pode ter ID, se quiser atualizar use o method PUT");
				return;
			}
			
			
			//inseri no banco e busca objeto persistido
			
			em.getTransaction().begin();
			
			Avaliacao avaliacaoDoBanco = (Avaliacao) new AvaliacaoDAO(em).Insert(avaliacao);
			
			em.getTransaction().commit();
			em.close();
			
			result.use(Results.json()).from(avaliacaoDoBanco).serialize();
		}finally {
			EntityManagerService.LiberaConnection(em);
		}
		
	}
	
	@Put("/avaliacoes/{avaliacao.id}")
	@Consumes("application/json")
	public void atualizaAvaliacao(Avaliacao avaliacao) {
		
		EntityManager em = new JPAUtil().getEntityManager();
		try {
			//validacoes
			if(avaliacao.getId() < 1) {
				result.use(Results.http()).sendError(400, "O avaliacao deve ter ID apra ser atualizado, se quiser inserir um novo utilize o POST");
				return;
			}
			
			//inseri no banco e busca objeto persistido
			
			AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO(em);
			
			Avaliacao avaliacaoDoBanco = (Avaliacao) avaliacaoDAO.SelectPorId(avaliacao);
			if(avaliacaoDoBanco != null) {
				
				
				em.getTransaction().begin();
				
					avaliacaoDAO.InsertOrUpdate(avaliacao);
				
				em.getTransaction().commit();
				em.close();
				
				result.use(Results.json()).from(avaliacaoDoBanco).serialize();
				
				
			}else {
				result.use(Results.http()).sendError(400, "Com o ID informado não existe nenhum avaliacao no banco");
				return;
		}	
		}finally {
			EntityManagerService.LiberaConnection(em);
		}
		
	}
	
	@Delete("/avaliacoes/{avaliacao.id}")
	public void deletaAvaliacao(Avaliacao avaliacao){
		
		EntityManager em = new JPAUtil().getEntityManager();
		try {
			//validacao
			if(avaliacao.getId() < 1) {
				result.use(Results.http()).sendError(400, "O avaliacao deve ter ID para ser deletado, se quiser inserir um novo utilize o POST");
				return;
			}
			
			
			AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO(em);
			
			Avaliacao avaliacaoDoBanco = (Avaliacao) avaliacaoDAO.SelectPorId(avaliacao);
			if(avaliacaoDoBanco != null) { //verifica se existe avaliacao com aquele ID
				
				
				em.getTransaction().begin();
				
					avaliacaoDAO.Delete(avaliacaoDoBanco);
				
				em.getTransaction().commit();
				em.close();
				
				result.use(Results.json()).from(avaliacaoDoBanco).serialize();
				
				
			}else {
				result.use(Results.http()).sendError(400, "Com o ID informado não existe nenhum avaliacao no banco");
				return;
			}
		}finally {
			EntityManagerService.LiberaConnection(em);
		}
	}
	
}
